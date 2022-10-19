postscript("DTLZ.IGD.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data/"
qIndicator <- function(indicator, problem)
{
fileMOEAD<-paste(resultDirectory, "MOEAD", sep="/")
fileMOEAD<-paste(fileMOEAD, problem, sep="/")
fileMOEAD<-paste(fileMOEAD, indicator, sep="/")
MOEAD<-scan(fileMOEAD)

fileMOEADEA<-paste(resultDirectory, "MOEADEA", sep="/")
fileMOEADEA<-paste(fileMOEADEA, problem, sep="/")
fileMOEADEA<-paste(fileMOEADEA, indicator, sep="/")
MOEADEA<-scan(fileMOEADEA)

fileMOEADEA1<-paste(resultDirectory, "MOEADEA1", sep="/")
fileMOEADEA1<-paste(fileMOEADEA1, problem, sep="/")
fileMOEADEA1<-paste(fileMOEADEA1, indicator, sep="/")
MOEADEA1<-scan(fileMOEADEA1)

algs<-c("MOEAD","MOEADEA","MOEADEA1")
boxplot(MOEAD,MOEADEA,MOEADEA1,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(3,3))
indicator<-"IGD"
qIndicator(indicator, "DTLZ1")
qIndicator(indicator, "DTLZ2")
qIndicator(indicator, "DTLZ3")
qIndicator(indicator, "DTLZ4")
qIndicator(indicator, "DTLZ5")
qIndicator(indicator, "DTLZ6")
qIndicator(indicator, "DTLZ7")
