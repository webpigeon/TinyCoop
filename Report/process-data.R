setwd("M:\\graphs")

library(ggplot2) 
games <- read.csv(file="m:\\combined.csv", head=TRUE,sep=",")

scores <- aggregate(games$score, list(agent1=games$agent1), mean)
png(filename="scores-allmaps.png", width = 480, height = 480, units="px")
mp <- barplot(scores$x, names.arg=scores$agent1, ylab="Scores", ylim=c(0,1),axisnames = FALSE)
text(mp, par("usr")[3], labels = scores$agent1, srt = 45, adj = 1, xpd = TRUE)
axis(2)
invisible(dev.off())

ticks <- aggregate(games$ticks, list(agent1=games$agent1), mean)
png(filename="ticks-allmaps.png", width = 480, height = 480, units="px")
mp <- barplot(ticks$x, names.arg=ticks$agent1, ylab="ticks", ylim=c(0,2000),axisnames = FALSE)
text(mp, par("usr")[3], labels = ticks$agent1, srt = 45, adj = 1, xpd = TRUE)
axis(2)
invisible(dev.off())

for (map in unique(games$maps)) {
	filtered <- games[games$maps==map, ]
	scores <- aggregate(filtered$scores, list(agent1=filtered$agent1), mean)
	ticks <- aggregate(filtered$ticks, list(agent1=filtered$agent1), mean)

	nameScores = paste(map,"-scores.png",sep="")
	png(filename=nameScores, width = 480, height = 480, units="px")
	mp <- barplot(scores$x, names.arg=scores$agent1, ylab="Scores", ylim=c(0,1), axisnames = FALSE)
	text(mp, par("usr")[3], labels = scores$agent1, srt = 45, adj = 1, xpd = TRUE)
	axis(2)
	invisible(dev.off())

	nameTicks  = paste(map,"-ticks.png",sep="")
	png(filename=nameTicks, width = 480, height = 480, units="px")
	mp <- barplot(ticks$x, names.arg=ticks$agent1, ylab="ticks", ylim=c(0,2000), axisnames = FALSE)
	text(mp, par("usr")[3], labels = ticks$agent1, srt = 45, adj = 1, xpd = TRUE)
	axis(2)
	invisible(dev.off())
}