postscript("DTLZ.IGD.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data/"
qIndicator <- function(indicator, problem)
{
fileMOEAD<-paste(resultDirectory, "MOEAD", sep="/")
fileMOEAD<-paste(fileMOEAD, problem, sep="/")
fileMOEAD<-paste(fileMOEAD, indicator, sep="/")
MOEAD<-scan(fileMOEAD)

fileMOEADEA11<-paste(resultDirectory, "MOEADEA11", sep="/")
fileMOEADEA11<-paste(fileMOEADEA11, problem, sep="/")
fileMOEADEA11<-paste(fileMOEADEA11, indicator, sep="/")
MOEADEA11<-scan(fileMOEADEA11)

fileMOEADEA15<-paste(resultDirectory, "MOEADEA15", sep="/")
fileMOEADEA15<-paste(fileMOEADEA15, problem, sep="/")
fileMOEADEA15<-paste(fileMOEADEA15, indicator, sep="/")
MOEADEA15<-scan(fileMOEADEA15)

fileMOEADEA33<-paste(resultDirectory, "MOEADEA33", sep="/")
fileMOEADEA33<-paste(fileMOEADEA33, problem, sep="/")
fileMOEADEA33<-paste(fileMOEADEA33, indicator, sep="/")
MOEADEA33<-scan(fileMOEADEA33)

fileMOEADEA55<-paste(resultDirectory, "MOEADEA55", sep="/")
fileMOEADEA55<-paste(fileMOEADEA55, problem, sep="/")
fileMOEADEA55<-paste(fileMOEADEA55, indicator, sep="/")
MOEADEA55<-scan(fileMOEADEA55)

fileMOEADGenEA<-paste(resultDirectory, "MOEADGenEA", sep="/")
fileMOEADGenEA<-paste(fileMOEADGenEA, problem, sep="/")
fileMOEADGenEA<-paste(fileMOEADGenEA, indicator, sep="/")
MOEADGenEA<-scan(fileMOEADGenEA)

algs<-c("MOEAD","MOEADEA11","MOEADEA15","MOEADEA33","MOEADEA55","MOEADGenEA")
boxplot(MOEAD,MOEADEA11,MOEADEA15,MOEADEA33,MOEADEA55,MOEADGenEA,names=algs, notch = FALSE)
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
