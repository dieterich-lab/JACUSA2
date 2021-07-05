#Read preprocessed JACUSA2 output
eins=read.delim("call2_SitesExt2_indel_slim2.txt",as.is=T,header=F)
rtarrest=read.delim("rtArrest_SitesExt2_slim2.txt",as.is=T,header=F)
#Formatting
colnames(eins)=c("ID","contig","position","RT","call2.score","deletion.score","insertion.score","Base","Anchor","Strand")
colnames(rtarrest)=c("ID","contig","position","RT","arrest.score","Base","Anchor","Strand")

eins=subset(eins,eins$Base %in% c("T","psU"))
rtarrest=subset(rtarrest,rtarrest$Base %in% c("T","psU"))

#Split by experiment
SIIIRTMn=subset(eins,eins$RT=="rmDup_SIIIRTMn_RC22_call2_result.out")
SIIIRTMg=subset(eins,eins$RT=="rmDup_SIIIRTMg_RC22_call2_result.out")
HIVRT=subset(eins,eins$RT=="rmDup_HIVRT_RC22_call2_result.out")


Call2=tapply(SIIIRTMn$call2.score,list(SIIIRTMn$ID,SIIIRTMn$Anchor),sum)
Call2[is.na(Call2)]<-0
colnames(Call2)<-paste0("SIIIRTMnCall2Score_",colnames(Call2))

Deletion=tapply(SIIIRTMn$deletion.score,list(SIIIRTMn$ID,SIIIRTMn$Anchor),sum)
Deletion[is.na(Deletion)]<-0
colnames(Deletion)<-paste0("SIIIRTMnDeletionScore_",colnames(Deletion))

Insertion=tapply(SIIIRTMn$insertion.score,list(SIIIRTMn$ID,SIIIRTMn$Anchor),sum)
Insertion[is.na(Insertion)]<-0
colnames(Insertion)<-paste0("SIIIRTMnInsertionScore_",colnames(Insertion))

BigTable=merge(data.frame(ID=rownames(Call2),Call2),data.frame(ID=rownames(Deletion),Deletion),by.x=1,by.y=1)
BigTable=merge(BigTable,data.frame(ID=rownames(Insertion),Insertion),by.x=1,by.y=1)

Call2=tapply(SIIIRTMg$call2.score,list(SIIIRTMg$ID,SIIIRTMg$Anchor),sum)
Call2[is.na(Call2)]<-0
colnames(Call2)<-paste0("SIIIRTMgCall2Score_",colnames(Call2))

Deletion=tapply(SIIIRTMg$deletion.score,list(SIIIRTMg$ID,SIIIRTMg$Anchor),sum)
Deletion[is.na(Deletion)]<-0
colnames(Deletion)<-paste0("SIIIRTMgDeletionScore_",colnames(Deletion))

Insertion=tapply(SIIIRTMg$insertion.score,list(SIIIRTMg$ID,SIIIRTMg$Anchor),sum)
Insertion[is.na(Insertion)]<-0
colnames(Insertion)<-paste0("SIIIRTMgInsertionScore_",colnames(Insertion))

BigTable=merge(BigTable,data.frame(ID=rownames(Call2),Call2),by.x=1,by.y=1)
BigTable=merge(BigTable,data.frame(ID=rownames(Deletion),Deletion),by.x=1,by.y=1)
BigTable=merge(BigTable,data.frame(ID=rownames(Insertion),Insertion),by.x=1,by.y=1)

Call2=tapply(HIVRT$call2.score,list(HIVRT$ID,HIVRT$Anchor),sum)
Call2[is.na(Call2)]<-0
colnames(Call2)<-paste0("HIVRTCall2Score_",colnames(Call2))

Deletion=tapply(HIVRT$deletion.score,list(HIVRT$ID,HIVRT$Anchor),sum)
Deletion[is.na(Deletion)]<-0
colnames(Deletion)<-paste0("HIVRTDeletionScore_",colnames(Deletion))

Insertion=tapply(HIVRT$insertion.score,list(HIVRT$ID,HIVRT$Anchor),sum)
Insertion[is.na(Insertion)]<-0
colnames(Insertion)<-paste0("HIVRTInsertionScore_",colnames(Insertion))

BigTable=merge(BigTable,data.frame(ID=rownames(Call2),Call2),by.x=1,by.y=1)
BigTable=merge(BigTable,data.frame(ID=rownames(Deletion),Deletion),by.x=1,by.y=1)
BigTable=merge(BigTable,data.frame(ID=rownames(Insertion),Insertion),by.x=1,by.y=1)

#exit(0);
                                        #rtarrest
#Split by experiment
SIIIRTMn=subset(rtarrest,rtarrest$RT=="rmDup_SIIIRTMn_RC22_rtarrest_plain_result.out")
SIIIRTMg=subset(rtarrest,rtarrest$RT=="rmDup_SIIIRTMg_RC22_rtarrest_plain_result.out")
HIVRT=subset(rtarrest,rtarrest$RT=="rmDup_HIVRT_RC22_rtarrest_plain_result.out")

Arrest=tapply(SIIIRTMn$arrest.score,list(SIIIRTMn$ID,SIIIRTMn$Anchor),sum)
Arrest[is.na(Arrest)]<-0
colnames(Arrest)<-paste0("SIIIRTMnArrestScore_",colnames(Arrest))

BigTable=merge(BigTable,data.frame(ID=rownames(Arrest),Arrest),by.x=1,by.y=1)

Arrest=tapply(SIIIRTMg$arrest.score,list(SIIIRTMg$ID,SIIIRTMg$Anchor),sum)
Arrest[is.na(Arrest)]<-0
colnames(Arrest)<-paste0("SIIIRTMgArrestScore_",colnames(Arrest))

BigTable=merge(BigTable,data.frame(ID=rownames(Arrest),Arrest),by.x=1,by.y=1)

Arrest=tapply(HIVRT$arrest.score,list(HIVRT$ID,HIVRT$Anchor),sum)
Arrest[is.na(Arrest)]<-0
colnames(Arrest)<-paste0("HIVRTArrestScore_",colnames(Arrest))

BigTable=merge(BigTable,data.frame(ID=rownames(Arrest),Arrest),by.x=1,by.y=1)

BigTableLabel=merge(BigTable,unique(eins[,c("ID","Base")]),by.x=1,by.y=1)
#exit(0);



saveRDS(BigTableLabel, file="BigTableLabel.rds")

#saveRDS(BigTableLabel, file="Big2TableLabel.rds")


require(ggplot2)
require(plotROC)

#aga<-BigTableLabel
rocdataF <- data.frame(D = BigTableLabel$"Base", M=BigTableLabel$"HIVRTCall2Score_1", N=BigTableLabel$"HIVRTArrestScore_2"+BigTableLabel$"HIVRTCall2Score_1")
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

rocdataF <- data.frame(D = aga$"ClassLabel", HIVRT=aga$"HIV.arrest.score.plus1"+aga$"HIV.call2.score", SIIIRTMg=aga$"SMg.arrest.score.plus1"+aga$"SMg.call2.score", SIIIRTMn=aga$"SMn.arrest.score.plus1"+aga$"SMn.call2.score")
rocdataF <- melt(rocdataF)
p<-ggplot(rocdataF, aes(m = value, d = D, color=variable)) + geom_roc(labels=F)
calc_auc(p)

ggsave("/Users/cdieterich/repository/JACUSAnalysis/manuscript/bmc_template/figures/ROC_3RT_Dec2020.pdf")

#transfer to caret 
#üarty with the mob

save(aga,file="Basic_Tao_df.Rdat")
exit(0);

                                        #mlp

