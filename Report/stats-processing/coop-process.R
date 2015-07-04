setwd("D:\\graphs")
library(ggplot2) 

# custom functions
alpha<-0.05 # for a (1.00-alpha)=95% confidence interval
c <- function() rm(list=ls())
se <- function(x) sd(x) / sqrt(length(x))

build_summary_score <- function (x) {
	s <- data.frame(
    		agent1=levels(x$agent1),
    		mean=tapply(x$score, x$agent1, mean),
    		n=tapply(x$score, x$agent1, length),
    		sd=tapply(x$score, x$agent1, sd),
    		se=tapply(x$score, x$agent1, se)
	)
	# Precalculate standard error of the mean (SEM)
	s$sem <- s$sd/sqrt(s$n)
	# Precalculate margin of error for confidence interval
	s$me <- qt(1-alpha/2, df=s$n)*s$sem
	return(s)
}

build_summary_ticks <- function (x) {
	s <- data.frame(
    		agent1=levels(x$agent1),
    		mean=tapply(x$ticks, x$agent1, mean),
    		n=tapply(x$ticks, x$agent1, length),
    		sd=tapply(x$ticks, x$agent1, sd),
    		se=tapply(x$ticks, x$agent1, se)
	)
	# Precalculate standard error of the mean (SEM)
	s$sem <- s$sd/sqrt(s$n)
	# Precalculate margin of error for confidence interval
	s$me <- qt(1-alpha/2, df=s$n)*s$sem
	return(s)
}

build_graph <- function (filename, title, s) {
	summary(s)
	png(filename, width = 900, height = 500, units="px")
	p = ggplot(s, aes(x = agent1, y = mean)) +  
  	geom_bar(position = position_dodge(), stat="identity", fill="blue") + 
  	geom_errorbar(aes(ymin=mean-sem, ymax=mean+sem)) +
  	ggtitle(title) + 
  	theme_bw() +
  	theme(panel.grid.major = element_blank())
	print(p)
	invisible(dev.off())
}

#read in our raw data
games.raw <- read.csv(file="combined.csv", head=TRUE,sep=",")

# build graphs of agent and it's self
samePairs.raw = games.raw[games.raw$agent1==games.raw$agent2,]
samePairs.scores <- build_summary_score(samePairs.raw)
samePairs.ticks <- build_summary_ticks(samePairs.raw)
build_graph("scores-samepairs.png", "All agents paired with themselves", samePairs.scores)
build_graph("ticks-samepairs.png", "All agents paired with themselves", samePairs.ticks)

# build RR graphs for all agents
games.scores <- build_summary_score(games.raw)
games.ticks <- build_summary_ticks(games.raw)
build_graph("scores-allmaps.png", "Scores across all maps for all agents", games.scores)
build_graph("ticks-allmaps.png", "Ticks across all maps for all agents", games.ticks)

# All pairs avg score all maps
#scores <- aggregate(games$score, list(agent1=games$agent1, agent2=games$agent2), mean)
#png(filename="pairscores.png", width = 720, height = 480, units="px")
#scores <- aggregate(games$score, list(agent1=games$agent1, agent2=games$agent2), mean)
#barchart(scores$x ~ scores$agent1, groups=scores$agent2, ylab="Scores", ylim=c(0,1), auto.key=list(space="top", columns=4))
#invisible(dev.off())

for (map in unique(games.raw$maps)) {
	filtered.raw <- games.raw[games.raw$maps==map, ]
	filtered.scores <- build_summary_score(filtered.raw)
	filtered.ticks <- build_summary_ticks(filtered.raw)
	mapper <- gsub("[.]", "-", map)
	name.scores = paste(mapper,"-scores.png",sep="")
	name.ticks = paste(mapper,"-ticks.png",sep="")
	build_graph(name.scores, "All agents across a single map", filtered.scores)
	build_graph(name.ticks, "All agents across a single map", filtered.ticks)

	samePairs.raw = filtered.raw[filtered.raw$agent1==filtered.raw$agent2,]
	samePairs.scores <- build_summary_score(samePairs.raw)
	samePairs.ticks <- build_summary_ticks(samePairs.raw)

	name.scores = paste(mapper,"-pairs-scores.png",sep="")
	name.ticks = paste(mapper,"-pairs-ticks.png",sep="")
	build_graph(name.scores, "All agents paired with themselves", samePairs.scores)
	build_graph(name.ticks, "All agents paired with themselves", samePairs.ticks)
}