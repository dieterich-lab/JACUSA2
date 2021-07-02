TaoReadSignatures=readRDS("BigTableLabel.rds")

df=TaoReadSignatures[,-1]
colnames(df)[ncol(df)]<-"label"

df$label=as.factor(df$label)

df=df[,c(grep("Call2Score_1",colnames(df)),grep("ArrestScore_2",colnames(df)),ncol(df))]
#library(doParallel)
#cl <- makePSOCKcluster(2)
#registerDoParallel(cl)

set.seed(825)
S2Tab=data.frame(TaoReadSignatures[,"ID"],label=df$label,pred=df$label)

ListOfNNObj<-list()
require("caret")

j <- createFolds(df$label, list = FALSE, k=10)
for(f in 1:10)
{
        training <- df[which(j!=f),]
        testing  <- df[which(j==f),]

        fitControl <- trainControl(## 10-fold CV
                           method = "none")
                           #number = 5,
                           ## repeated ten times
                           #repeats = 5)

        fit<-caret::train(label ~ ., data = training, 
                 method = "mlp", 
                 trControl = fitControl,size=3,
                 preProc = c("center", "scale"),
                 metric = "Accuracy")

print(paste0("Fit done ..",f));
#stopCluster(cl)


ttestNN=caret::confusionMatrix(reference=testing$label,data=factor(predict(fit,testing)),positive="psU")
ttrainNN=caret::confusionMatrix(reference = training$label, data = factor(predict(fit,training)),positive="psU")

        S2Tab[which(j==f),"pred"]<-factor(predict(fit,testing))
        
print(ttestNN)
ListOfNNObj[[f]]<-ttestNN
}

require(openxlsx)
wb <- createWorkbook()
addWorksheet(wb, sheetName = "rRNA");
writeDataTable(wb, sheet = 1, x=merge(TaoReadSignatures[,c("ID",colnames(df)[1:6])],S2Tab,by.x=1,by.y=1,all.x=T));
saveWorkbook(wb,file="Supplementary_Table_2.xlsx",overwrite=T)


require(plotROC)
require(reshape2)

rocdataF <- data.frame(D = df$"label", HIVRT=df$"HIVRTArrestScore_2"+df$"HIVRTCall2Score_1", SIIIRTMg=df$"SIIIRTMgArrestScore_2"+df$"SIIIRTMgCall2Score_1", SIIIRTMn=df$"SIIIRTMnArrestScore_2"+df$"SIIIRTMnCall2Score_1")
rocdataF <- melt(rocdataF)

yAx=as.numeric(sapply(ListOfNNObj,function(x){x$byClass["Sensitivity"]}))
xAx=1-as.numeric(sapply(ListOfNNObj,function(x){x$byClass["Specificity"]}))

p<-ggplot(rocdataF, aes(m = value, d = D, color=variable)) + geom_roc(labels=F) +
geom_pointrange(aes(x=median(xAx), y=median(yAx), ymin=min(yAx), ymax=max(yAx), colour="Classifier_NN")) +
  geom_errorbarh(aes(x=median(xAx), y=median(yAx), xmin=min(xAx), xmax=max(xAx) , colour="Classifier_NN", height = 0))
p

ggsave("ROC_3RT_NN_2021.pdf")

calc_auc(p)


