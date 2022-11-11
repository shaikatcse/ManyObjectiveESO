pdf("DTLZ.Boxplot.pdf",  onefile=FALSE, height=8, width=10)
resultDirectory<-"../data/"
qIndicator <- function(indicator, problem)
{
fileMOEAD<-paste(resultDirectory, "MOEAD", sep="/")
fileMOEAD<-paste(fileMOEAD, problem, sep="/")
fileMOEAD<-paste(fileMOEAD, indicator, sep="/")
MOEAD<-scan(fileMOEAD)

fileNSGAII<-paste(resultDirectory, "NSGAII", sep="/")
fileNSGAII<-paste(fileNSGAII, problem, sep="/")
fileNSGAII<-paste(fileNSGAII, indicator, sep="/")
NSGAII<-scan(fileNSGAII)

fileSPEA2<-paste(resultDirectory, "SPEA2", sep="/")
fileSPEA2<-paste(fileSPEA2, problem, sep="/")
fileSPEA2<-paste(fileSPEA2, indicator, sep="/")
SPEA2<-scan(fileSPEA2)

algs<-c("MOEAD","NSGAII","SPEA2")
boxplot(MOEAD,NSGAII,SPEA2,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(3,3))
indicator<-"HV"
qIndicator(indicator, "DTLZ1")
qIndicator(indicator, "DTLZ2")
qIndicator(indicator, "DTLZ3")
qIndicator(indicator, "DTLZ4")
qIndicator(indicator, "DTLZ5")
qIndicator(indicator, "DTLZ6")
qIndicator(indicator, "DTLZ7")
