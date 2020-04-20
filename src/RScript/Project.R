
library(Rmisc)
library("tidyverse")
library("ggplot2")

x_gen_stats <- read.csv("SAT3_GA_gen_stats.csv", stringsAsFactors=FALSE)

	
x_overall_stats <- x_gen_stats %>% 
	summarize(bestFit = max(bestF), avgF = mean(bestF), stdF = sd(bestF), GCI = (CI(bestF, ci = 0.95)[1] - CI(bestF, ci = 0.95)[2]))

x_avg_best <- x_gen_stats %>% 
	group_by(G) %>% 
	summarize(F = mean(bestF), stdF = sd(bestF), GCI = (CI(bestF, ci = 0.95)[1] - CI(bestF, ci = 0.95)[2]))

x_avg_best <- mutate(x_avg_best, Type = "Best")

x_avg_avg <- x_gen_stats %>% 
	group_by(G) %>% 
	summarize(F = mean(avgF), stdF = sd(avgF), GCI = (CI(avgF, ci = 0.95)[1] - CI(avgF, ci = 0.95)[2]))

x_avg_avg <- mutate(x_avg_avg, Type = "Average")

x_avg <- rbind(x_avg_best, x_avg_avg)

x_avg$Type <- as.factor(x_avg$Type)

# 3-SAT Deterministic Crowding GA - Number of Variables = 200
# 3-SAT Standard GA - Number of Variables = 125

x_avg <- replace(x_avg, is.na(x_avg), 0)

ggplot(x_avg, aes(x=G, y=F)) +
    geom_errorbar(aes(ymin=F-GCI, ymax=F+GCI, color = Type), size = 1, alpha = 0.5) +
	geom_line(aes(group=Type), size = 0.3, color="black") +
	labs(title="3-SAT Standard GA - Number of Variables = 125\nAverage and best fitness with 95% confidence intervals,\naveraged over 1000 runs", x ="Generation", y = "Fitness") + 
	scale_color_manual(values = c("blue", "red"))


ggsave("3-SAT Standard GA - Number of Variables 125.png", width = 7, height = 4)

# -------------------------------------------------------------------------------------------------------------

# 20
x_opt <- x_gen_stats %>% filter(bestF == 91)

# 50
x_opt <- x_gen_stats %>% filter(bestF == 218)

# 75
x_opt <- x_gen_stats %>% filter(bestF == 325)

# 100
x_opt <- x_gen_stats %>% filter(bestF == 430)

# 125
x_opt <- x_gen_stats %>% filter(bestF == 538)

# 175
x_opt <- x_gen_stats %>% filter(bestF == 753)

# 200
x_opt <- x_gen_stats %>% filter(bestF == 860)

# --------------

x_opt_rate <- x_opt %>% 
	group_by(XR) %>% 
	summarize(Rate = n() / 100, stdF = sd(avgF), GCI = (CI(avgF, ci = 0.95)[1] - CI(avgF, ci = 0.95)[2]))

x_opt_rate_overall <- x_opt_rate %>% 
	summarize(bestRate = max(Rate), avgRate = mean(Rate), stdF = sd(Rate), GCI = (CI(Rate, ci = 0.95)[1] - CI(Rate, ci = 0.95)[2]))
	
x_opt_rate_overall <- replace(x_opt_rate_overall, is.na(x_opt_rate_overall), 0)
x_opt_rate_overall[x_opt_rate_overall == -Inf || x_opt_rate_overall == -Inf] <- 0

save(x_opt_rate_overall,file="rate125.Rda")
	
# -------------------------------------------------------------------------------------------------------------

x_opt_gen_overall <- x_opt %>% 
	summarize(bestGen = min(G), avgGen = mean(G), stdGen = sd(G), GCI = (CI(G, ci = 0.95)[1] - CI(G, ci = 0.95)[2]))
	
x_opt_gen_overall <- replace(x_opt_gen_overall, is.na(x_opt_gen_overall), 0)
x_opt_gen_overall[x_opt_gen_overall == -Inf || x_opt_gen_overall == Inf] <- 0

save(x_opt_gen_overall,file="gen125.Rda")
		
# -------------------------------------------------------------------------------------------------------------
	
load("rate20.Rda")

x_rate <- mutate(x_opt_rate_overall, N = "20")

load("rate50.Rda")

x_rate <- rbind(x_rate, mutate(x_opt_rate_overall, N = "50"))
	
load("rate75.Rda")

x_rate <- rbind(x_rate, mutate(x_opt_rate_overall, N = "75"))
	
load("rate100.Rda")

x_rate <- rbind(x_rate, mutate(x_opt_rate_overall, N = "100"))
	
load("rate125.Rda")

x_rate <- rbind(x_rate, mutate(x_opt_rate_overall, N = "125"))
	
load("rate175.Rda")

x_rate <- rbind(x_rate, mutate(x_opt_rate_overall, N = "175"))
	
load("rate200.Rda")

x_rate <- rbind(x_rate, mutate(x_opt_rate_overall, N = "200"))
	
x_rate$N <- factor(x_rate$N, levels = c("20", "50", "75", "100", "125", "175", "200"))

# Success Rate of 3-SAT Deterministic Crowding GA per Number of Variables


ggplot(x_rate, aes(x=N, group = 1)) +
    geom_errorbar(aes(ymin=avgRate-GCI, ymax=avgRate+GCI), width=0.2, size = 0.8, alpha = 0.6) +
	geom_line(aes(y=avgRate, color="blue")) +	
	geom_line(aes(y=bestRate, color="red")) + 
	labs(title="Success Rate of 3-SAT Deterministic Crowding GA per Number of Variables\nAverage rate with 95% confidence intervals,\naveraged over 10 runs", x ="Number of Variables", y = "Success Rate") +
	scale_colour_manual(values=c("blue", "red"), 
                       name="",
                       labels=c("Average", "Best"))


ggsave("Success Rate of 3-SAT Deterministic Crowding GA per Number of Variables.png", width = 7, height = 4)
	
		
# -------------------------------------------------------------------------------------------------------------
	
load("gen20.Rda")

x_gen <- mutate(x_opt_gen_overall, N = "20")

load("gen50.Rda")

x_gen <- rbind(x_gen, mutate(x_opt_gen_overall, N = "50"))
	
load("gen75.Rda")

x_gen <- rbind(x_gen, mutate(x_opt_gen_overall, N = "75"))
	
load("gen100.Rda")

x_gen <- rbind(x_gen, mutate(x_opt_gen_overall, N = "100"))
	
load("gen125.Rda")

x_gen <- rbind(x_gen, mutate(x_opt_gen_overall, N = "125"))
	
load("gen175.Rda")

x_gen <- rbind(x_gen, mutate(x_opt_gen_overall, N = "175"))
	
load("gen200.Rda")

x_gen <- rbind(x_gen, mutate(x_opt_gen_overall, N = "200"))
	
x_gen$N <- factor(x_gen$N, levels = c("20", "50", "75", "100", "125", "175", "200"))

# Average Earliest Generation to Find Optimum Solution of 3-SAT using\nDeterministic Crowding GA per Number of Variables

ggplot(x_gen, aes(x=N, group = 1)) +
    geom_errorbar(aes(ymin=avgGen-GCI, ymax=avgGen+GCI), width=0.1, size = 0.8, alpha = 0.6) +
	geom_line(aes(y=avgGen, color="blue")) +
	geom_line(aes(y=bestGen, color="red")) + 
	labs(title="Earliest Generation to Find Optimum Solution of 3-SAT using\nDeterministic Crowding GA per Number of Variables\n95% confidence intervals averaged over 10 runs", x ="Number of Variables", y = "Generation")  +
	scale_colour_manual(values=c("blue", "red"), 
                       name="",
                       labels=c("Average", "Best"))

ggsave("Earliest Generation to Find Optimum Solution of 3-SAT using Deterministic Crowding.png", width = 7, height = 4)






