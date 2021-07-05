#Read preprocessed JACUSA2 output
print("Read")
eins=read.delim("call2_SitesExt2_indel_slim.txt.gz",as.is=T,header=F)

#gzip
                                        #Formatting
print("Format")
colnames(eins)=c("ID","contig","position","RT","call2.score","deletion.score","insertion.score","Base","Anchor","Strand")

#Split by experiment
Exp1=subset(eins,eins$RT=="WT_vs_KO_2samp_RC22_call2_result.out")
Exp2=subset(eins,eins$RT=="WT100_vs_WT0_RC22_call2_result.out")

print("Exp1")
Call2=tapply(Exp1$call2.score,list(Exp1$ID,Exp1$Anchor),sum)
Call2[is.na(Call2)]<-0
colnames(Call2)<-paste0("Exp1Call2Score_",colnames(Call2))

Deletion=tapply(Exp1$deletion.score,list(Exp1$ID,Exp1$Anchor),sum)
Deletion[is.na(Deletion)]<-0
colnames(Deletion)<-paste0("Exp1DeletionScore_",colnames(Deletion))

Insertion=tapply(Exp1$insertion.score,list(Exp1$ID,Exp1$Anchor),sum)
Insertion[is.na(Insertion)]<-0
colnames(Insertion)<-paste0("Exp1InsertionScore_",colnames(Insertion))

BigTable=merge(data.frame(ID=rownames(Call2),Call2),data.frame(ID=rownames(Deletion),Deletion),by.x=1,by.y=1)
BigTable=merge(BigTable,data.frame(ID=rownames(Insertion),Insertion),by.x=1,by.y=1)

print("Exp2")#

Call2=tapply(Exp2$call2.score,list(Exp2$ID,Exp2$Anchor),sum)
Call2[is.na(Call2)]<-0
colnames(Call2)<-paste0("Exp2Call2Score_",colnames(Call2))

Deletion=tapply(Exp2$deletion.score,list(Exp2$ID,Exp2$Anchor),sum)
Deletion[is.na(Deletion)]<-0
colnames(Deletion)<-paste0("Exp2DeletionScore_",colnames(Deletion))

Insertion=tapply(Exp2$insertion.score,list(Exp2$ID,Exp2$Anchor),sum)
Insertion[is.na(Insertion)]<-0
colnames(Insertion)<-paste0("Exp2InsertionScore_",colnames(Insertion))


BigTable=merge(BigTable,data.frame(ID=rownames(Call2),Call2),by.x=1,by.y=1)
BigTable=merge(BigTable,data.frame(ID=rownames(Deletion),Deletion),by.x=1,by.y=1)
BigTable=merge(BigTable,data.frame(ID=rownames(Insertion),Insertion),by.x=1,by.y=1)

#Add sequence motif
motif=read.table("checkMotif_reformat.txt",as.is=T,header=F)
motif[,1]=gsub("-",":-",motif[,1])
motif[,1]=gsub("\\+",":\\+",motif[,1])

BigTable=merge(BigTable,motif,by.x=1,by.y=1);


colnames(BigTable)[ncol(BigTable)]="Motif"
rownames(BigTable)=BigTable[,1]
BigTable=BigTable[,-1]

BigTable$DRACH<-rep(0,nrow(BigTable))
BigTable$DRACH[grep("[AGT][AG]AC[ACT]",BigTable$Motif)]<-1

#Save table for processing
saveRDS(BigTable,file="BigTable.rds")

