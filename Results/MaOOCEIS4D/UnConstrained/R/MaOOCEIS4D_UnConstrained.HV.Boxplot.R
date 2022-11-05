pdf("MaOOCEIS4D_UnConstrained.HV.Boxplot.pdf",  onefile=FALSE, height=8, width=10)
resultDirectory<-"../"
qIndicator <- function(indicator, problem)
{
fileNSGAII<-paste(resultDirectory, "NSGAII", sep="/")
fileNSGAII<-paste(fileNSGAII, problem, sep="/")
fileNSGAII<-paste(fileNSGAII, indicator, sep="/")
NSGAII<-scan(fileNSGAII)

fileNSGAIII<-paste(resultDirectory, "NSGAIII", sep="/")
fileNSGAIII<-paste(fileNSGAIII, problem, sep="/")
fileNSGAIII<-paste(fileNSGAIII, indicator, sep="/")
NSGAIII<-scan(fileNSGAIII)

fileSPEA2<-paste(resultDirectory, "SPEA2", sep="/")
fileSPEA2<-paste(fileSPEA2, problem, sep="/")
fileSPEA2<-paste(fileSPEA2, indicator, sep="/")
SPEA2<-scan(fileSPEA2)

algs<-c("NSGAII","NSGAIII","SPEA2")
boxplot(NSGAII,NSGAIII,SPEA2,names=algs, notch = TRUE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(1,2))
indicator<-"HV"
qIndicator(indicator, "")
dev.off()