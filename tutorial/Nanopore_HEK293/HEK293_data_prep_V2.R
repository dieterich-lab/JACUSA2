#Load table from HEK293_data_prep.R
BigTable=readRDS("BigTable.rds")

Noverlap=scan("miCLIP_sites.txt",character())
Noverlap=intersect(rownames(BigTable),Noverlap)
NMFtab=BigTable[Noverlap,]

library(NMF)
nmfSeed('nndsvd')
meth <- nmfAlgorithm(version='R')
meth <- c(names(meth), meth)
NMFtabSlim=NMFtab[,1:30]

estim.r <- nmf(NMFtabSlim, 2:10, nrun=10, seed=123456, .opt='vp3')

V.random <- randomize(NMFtabSlim)
# estimate quality measures from the shuffled data (use default NMF algorithm)
estim.r.random <- nmf(V.random, 2:10, nrun=10, seed=123456, .opt='vp3')

pdf("NMF_assess.pdf")
plot(estim.r,estim.r.random)
dev.off()
#
res <- nmf(NMFtabSlim, 3, nrun=10, seed=123456, .opt='vp3')

saveRDS(res,file="NMF.rds")

exit(0);

###############
# Analyse signal #
###############

pdf("NMF_3cluster_new.pdf")
layout(cbind(1,2))
# basis components
basismap(res)
# mixture coefficients
aga<-coefmap(res)
dev.off()

w<-basis(res)
h <- coef(res)

# The palette with black:
cbbPalette <- c("#000000", "#E69F00", "#56B4E9", "#009E73", "#F0E442", "#0072B2", "#D55E00", "#CC79A7")

pdf("Pattern1_barplot_new.pdf",width=14,height=7)
split.screen(c(1, 2))
screen(1)
rep1=matrix(h[1,],ncol=5,byrow=T)[1:3,]
rownames(rep1)=c("BASE","DEL","INS")
colnames(rep1)=c("Pos1","Pos2","Pos3","Pos4","Pos5")
barplot(rep1,legend=F, main="Experiment 1",col=cbbPalette,,cex.names=2,cex.axis=2)
screen(2)
rep2=matrix(h[1,],ncol=5,byrow=T)[4:6,]
rownames(rep2)=c("BASE","DEL","INS")
colnames(rep2)=c("Pos1","Pos2","Pos3","Pos4","Pos5")
barplot(rep2,legend=F, main="Experiment 2",col=cbbPalette,,cex.names=2,cex.axis=2)
dev.off()

#check for presence of DRACH motif

bla=data.frame(w,DRACH=NMFtab[rownames(w),"DRACH"])
bla=bla[order(apply(bla[,1:3],1,max),decreasing=T),]

require(reshape2)
require(ggplot2)
                                        #Factor2 shows an association with DRACH .. wilcox.test
toplot=melt(apply(bla[,1:3],2,function(x){(x-mean(x))/sd(x)}))
#toplot=melt(bla[,1:5])

toplot$DRACH=rep(ifelse(bla$DRACH==1,"yes","no"),3)
colnames(toplot)=c("id","coeff","value","DRACH")
ggplot2::ggplot(toplot, ggplot2::aes(x = coeff, y = value, color=DRACH)) +
ggplot2::geom_boxplot() + scale_color_manual(values = c('#999999','#E69F00')) + theme_bw()

ggsave("NMF_loading_boxplot.pdf")

require(plotROC)
require(reshape2)
roci=toplot
rocdataF <- data.frame(D=roci$DRACH, M=roci$value, NMFCoeff = roci$coeff) #rowSums(smallTable[,1:6])
p=ggplot(rocdataF, aes(m = M, d = D, color=NMFCoeff)) + geom_roc(labels=F) + ggplot2::xlab("prop of none DRACH site") + ggplot2::ylab("prop of DRACH sites") + theme_bw()
p<-p+geom_abline(slope=1,intercept=0)

ggsave("ROC_plot_DRACH_NMF.pdf")


bla2=as.matrix(BigTable[,1:30])
bla3=bla2%*%h[2,]

dataGG=data.frame(Score=bla3)
dataGG$CLIP<-"no"
dataGG[Noverlap,"CLIP"]<-"yes"

xdensity <- ggplot(dataGG, aes(Score, color=CLIP)) + 
stat_ecdf() +
scale_color_manual(values = c('#999999','#0000FF')) + 
theme_bw()

ggsave("NMF1_ecdf.png",device="png")
saveRDS(dataGG,file="ScoreProfile_NMF1.rds")
