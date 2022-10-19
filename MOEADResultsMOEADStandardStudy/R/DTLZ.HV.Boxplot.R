postscript("DTLZ.HV.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data/"
qIndicator <- function(indicator, problem)
{
fileMOEAD<-paste(resultDirectory, "MOEAD", sep="/")
fileMOEAD<-paste(fileMOEAD, problem, sep="/")
fileMOEAD<-paste(fileMOEAD, indicator, sep="/")
MOEAD<-scan(fileMOEAD)

fileMOEADEA2<-paste(resultDirectory, "MOEADEA2", sep="/")
fileMOEADEA2<-paste(fileMOEADEA2, problem, sep="/")
fileMOEADEA2<-paste(fileMOEADEA2, indicator, sep="/")
MOEADEA2<-scan(fileMOEADEA2)

algs<-c("MOEAD","MOEADEA2")
boxplot(MOEAD,MOEADEA2,names=algs, notch = FALSE)
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
