pdf("MaOOCEIS4D.Boxplot.pdf",  onefile=FALSE, height=8, width=10)
resultDirectory<-"../"
qIndicator <- function(indicator, problem)
{
fileNSGAII<-paste(resultDirectory,problem, sep="/")
fileNSGAII<-paste(fileNSGAII,"NSGAII", sep="/")
fileNSGAII<-paste(fileNSGAII, indicator, sep="/")
NSGAII<-scan(fileNSGAII)

fileSPEA2<-paste(resultDirectory,problem, sep="/")
fileSPEA2<-paste(fileSPEA2,"SPEA2", sep="/")
fileSPEA2<-paste(fileSPEA2, indicator, sep="/")
SPEA2<-scan(fileSPEA2)

fileNSGAIII<-paste(resultDirectory,problem, sep="/")
fileNSGAIII<-paste(fileNSGAIII,"NSGAIII", sep="/")
fileNSGAIII<-paste(fileNSGAIII, indicator, sep="/")
NSGAIII<-scan(fileNSGAIII)

algs<-c("NSGAII","SPEA2","NSGAIII")
boxplot(NSGAII,SPEA2,NSGAIII,names=algs, notch = TRUE)
titulo <-paste(indicator,paste("MaOOCEIS4D_",problem), sep=":")
title(main=titulo)
}
par(mfrow=c(3,2))
indicator<-"HV"
qIndicator(indicator, "Unconstrained")
qIndicator(indicator, "Constrained")
indicator<-"IGD"
qIndicator(indicator, "Unconstrained")
qIndicator(indicator, "Constrained")
dev.off()