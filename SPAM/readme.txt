############
#INTRODUSION
############
#It is a pretty simple version of my filter. I started work on it with creating method's like "Check for common spammers pattern",
#contains "Number of words with no vowels", "Number of words with at least two of letters J, K, Q, X, Z", "Number of words with at least 15 characters",
#"Binary feature indicating whether the strings “From:” and “To:” were both present", e.t.c
#(Based on this work http://stat.wvu.edu/~dluo/CS791A/project_proposal.pdf).
#But when I writed it and check on training data set(461:SPAM, 153:HAM), all patterns triggered without any depending to human eyes.
#I visualized proportion words without vowels in spam and ham (http://goo.gl/XU3AA),(http://goo.gl/DnF6p), and its seems like neutral network needed
#to create working filter based on pattern like this. Dead end for me. 
############
#DESCRIPTION
############
#Filter already trained on data sets, have dicts with spamers and hamers list, and list of spam/ham email subjects. Dict's summ all trainigs.
#Filter already trained on data sets with Bayesian algorithm. Dicts sum only my big training
#When filter test data: then check first black list of spamers, white list of hamers, then subject spam/ham lists and then, if it is all new
#email message, start Bayesian algorithm.
###########
#TO DO
###########
#Write some algorithm to make work check_common_spamers_pattern module (https://github.com/il-vladislav/RPH/tree/master/SPAM)
#Try to ignore HTML code, when using Bayesian algorithm.
#There is much thing to do, Machine Learning never stop...
###########