pdf("MaOOCEIS4D_UnConstrained.Boxplot.pdf",  onefile=FALSE, height=8, width=10)
resultDirectory<-"../"
qIndicator <- function(indicator, problem)
{
fileNSGAII<-paste(resultDirectory,problem, sep="/")
fileNSGAII<-paste(fileNSGAII,"NSGAII", sep="/")
fileNSGAII<-paste(fileNSGAII, indicator, sep="/")
NSGAII<-scan(fileNSGAII)

fileNSGAIII<-paste(resultDirectory,problem, sep="/")
fileNSGAIII<-paste(fileNSGAIII,"NSGAIII", sep="/")
fileNSGAIII<-paste(fileNSGAIII, indicator, sep="/")
NSGAIII<-scan(fileNSGAIII)

fileSPEA2<-paste(resultDirectory,problem, sep="/")
fileSPEA2<-paste(fileSPEA2,"SPEA2", sep="/")
fileSPEA2<-paste(fileSPEA2, indicator, sep="/")
SPEA2<-scan(fileSPEA2)

algs<-c("NSGAII","NSGAIII","SPEA2")
boxplot(NSGAII,NSGAIII,SPEA2,names=algs, notch = TRUE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(2,2))
indicator<-"HV"
qIndicator(indicator, "Constrained")
qIndicator(indicator, "UnConstrained")
indicator<-"IGD"
qIndicator(indicator, "Constrained")
qIndicator(indicator, "UnConstrained")
dev.off()