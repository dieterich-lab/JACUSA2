#BigTable=readRDS("BigTable.rds")

#keep=rownames(BigTable)[which(is.na(BigTable$miCLIP)==F)]

Noverlap=scan("miCLIP_sites.txt",character())

Noverlap=intersect(rownames(BigTable),Noverlap)

NMFtab=BigTable[Noverlap,]
#selIDX=order(apply(NMFtab[,1:30],1,max),decreasing=T)
#blu=apply(NMFtab[,1:30],2,function(x){(x-mean(x))/sd(x)})
#NMFtab[,1:30]<-blu

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

                                        #init <- nmfModel(3, esGolub, W=0.5, H=0.3)
                                        #init=nmfModel(W=,H=coef(res)) bla[which(bla<0,arr.ind=T)]<-0
                                        #start from BigTable - oh well
saveRDS(res,file="NMF.rds")

exit(0);
#bla=BigTable[,1:30]
#bla[which(bla<0,arr.ind=T)]<-0
#bla[which(is.na(bla),arr.ind=T)]<-0
#bla=bla[-which(apply(bla,1,sum)==0),]

#init=nmfModel(W=rmatrix(1000, 5),H=coef(res))
#res2 <- nmf(bla[1:1000,], 5, nrun=10, seed=init, .opt='vp3')


###############
# >> |8+8| << #
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
#hplot<-h
#colnames(hplot)=gsub("Score","",colnames(hplot))
#idx=grep("Mettl3",colnames(hplot))
#colnames(hplot)[idx]=paste0(colnames(hplot)[idx],"_Exp1")

#idx=grep("Mettl14",colnames(hplot))
#colnames(hplot)[idx]=paste0(colnames(hplot)[idx],"_Exp2")

#colnames(hplot)=gsub("Mettl3","",colnames(hplot))
#colnames(hplot)=gsub("Mettl14","",colnames(hplot))
#colnames(hplot)=gsub("Call2","Sub",colnames(hplot))
#colnames(hplot)=gsub("Insertion","Ins",colnames(hplot))
#colnames(hplot)=gsub("Deletion","Del",colnames(hplot))

#pdf("Sweet_plot_of_mine.pdf")
#for(k in 1:5)
#    barplot(hplot[k,sort(colnames(hplot))],beside=T,las=2,col=cbbPalette[k],legend=c("Pattern1","Pattern2","Pattern3","Pattern4","Pattern5")[k])
#dev.off()#

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

#barplot(hplot[1,sort(colnames(hplot))],beside=T,las=2,col=cbbPalette[1],legend=c("Pattern1","Pattern2","Pattern3","Pattern4","Pattern5")[1])

#order by position and plot each profile separately #colors !

#ids=names(which(wScale[,3]>0.8))
# ok, we have 5 factors by now... we do not see a strong interaction across positions.
#bla, bla-.
#Story line 5mer->NMF->Factor2->ROC->MostDiscrFeature for both data sets.

                                        #smallTable=smallTable[order(smallTable$Mis3+,decreasing=T)[1:1000],]

#roci=melt(NMFtab[ids,c(order(h[3,],decreasing=T)[1:12],33)],id.vars="DRACH")
#rocdataF <- data.frame(D=roci$DRACH, M=roci$value, Z = roci$variable) #rowSums(smallTable[,1:6])
#p=ggplot(rocdataF, aes(m = M, d = D, color = Z)) + geom_roc(labels=F)
#p<-p+geom_abline(slope=1,intercept=0)

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

                                        #check sum approach pattern 1 from big matrix
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

                                        #PC1 looks good.


                                        #UMAP

                                        #switch to deep learning
                                        #shitte

#clustering...OLD STUFF

                                        #Looks good.. Motif logo stuff similar to MazF


#function
factor3=t(as.matrix((sapply(sapply(BigTable[rownames(smallTable),"Motif"],strsplit,""),unlist))))
factor3=apply(factor3,2,table)
#add function
factor3Mat=matrix(0,nrow=4,ncol=5)
rownames(factor3Mat)<-c("A","C","G","T")
colnames(factor3Mat)<-1:5
for(k in 1:5)
{
    for(l in names(factor3[[k]]))
    {
        factor3Mat[l,k]<-as.numeric(factor3[[k]][l])
    }
}

factor2=t(as.matrix((sapply(sapply(BigTable[names(which(wScale[,2]>0.6)),"Motif"],strsplit,""),unlist))))
factor2=apply(factor2,2,table)

#factor1=t(as.matrix((sapply(sapply(BigTable[names(which(wScale[,1]>0.6)),"Motif"],strsplit,""),unlist))))
#factor1=apply(factor1,2,table)

A=factor3Mat

bgA <- apply(A, 1, function(x) return(median(x)))
bgA <- bgA/sum(bgA)

bgm6A <- apply(m6A, 1, function(x) return(median(x)))
bgm6A <- bgm6A/sum(bgm6A)

pdf("All_sequences_3end.pdf")

logomaker(A, type = "Logo",bg=bgA)
logomaker(m6A, type = "Logo",bg=bgm6A)

logomaker(A, type = "EDLogo", bg=apply(m6A,2,function(x){x/sum(x)}))

logomaker(m6A, type = "EDLogo", bg=apply(A,2,function(x){x/sum(x)}))
dev.off();


pheatmap(h,scale="none",cluster_cols =F)

                                        #Start with PCA plot

PCA <- prcomp(na.omit(BigTableSlim), scale = F)

perc_var<-round(100*PCA$sdev^2/sum(PCA$sdev^2),1)

dataGG<-data.frame(PC1=PCA$x[,1],PC2=PCA$x[,2],PC3=PCA$x[,3])
p<-qplot(PC1,PC2,data=dataGG)+ggtitle("Nanopore")+xlab(paste("PC1 - explaining",perc_var[1],"% of variability"))+ylab(paste("PC2 - explaining",perc_var[2],"% of variability"))

p<-p + annotate("text", angle = 45, x = dataGG[,1], y = dataGG[,2], label = dataGG[,"animal"])


require(ggplot2)
require(plotROC)

rocdataF <- data.frame(D = aga$"ClassLabel", M=aga$"HIV.call2.score", N=aga$"HIV.arrest.score.plus1"+aga$"HIV.call2.score", Base_1= aga$"BasePlus1")
p<-ggplot(rocdataF, aes(m = N, d = D)) + geom_roc(labels=F)
#color = Base_1
p<-p+geom_abline(slope=1,intercept=0)
p
ggsave("/Users/cdieterich/repository/JACUSAtest/manuscript/bmc_template/figures/Tao_HIV_RT_score_sum_wo_deletion.pdf")

calc_auc(p)
#  PANEL group       AUC
#1     1     1 0.8340220
#2     1     2 0.9757140
#3     1     3 0.9484604
#4     1     4 0.8698830

#transfer to caret 
#üarty with the mob

save(aga,file="Basic_Tao_df.Rdat")
exit(0);

                                        #mlp

