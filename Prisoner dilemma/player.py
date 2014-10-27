class MyPlayer:
    """tit for tat with forgiveness"""
    def __init__(self,payoff_matrix, number_of_iterations=None):
        self.payoff_matrix=payoff_matrix
        self.number_of_iterations=number_of_iterations if number_of_iterations else []
        self.opponent_defects=0  #Opponent DEFECT in a row
        self.movecounter=0       #Move counter
        self.my_score=0          #My score
        self.opponent_score=0    #Opponent score
        self.cooperate=None      #Variable for COOPERATE (True or False)
        self.defect=None         #Variable for DEFECT (True or False)
        self.my_prev_move=None   #My previous move
        
        
    def record_opponents_move(self,opponent_move):
        self.opponent_move=opponent_move
        
        
    def move(self):
        #Payoff_matrix analyze
        coop_both=self.payoff_matrix[0][0][0]
        coop_me_def_you,def_you_coop_me=self.payoff_matrix[0][1]
        def_both=self.payoff_matrix[1][1][0]
        if (coop_both > def_both):
            self.cooperate=False
            self.defect=True
        if(coop_both < def_both):
            self.cooperate=True
            self.defect=False

        #Statistics
        if (self.my_prev_move==True):
            if(self.opponent_move==True):
                self.my_score += coop_both
                self.opponent_score += coop_both
            if(self.opponent_move==False):
                self.my_score += coop_me_def_you #[1]
                self.opponent_score += def_you_coop_me #[2]
        if (self.my_prev_move==False):
            if(self.opponent_move==True):
                self.my_score += def_you_coop_me #Vise versa [1]
                self.opponent_score += coop_me_def_you #Vise versa [2]
            if(self.opponent_move==False):
                self.my_score += def_both
                self.opponent_score += def_both
        
        #First move is always cooperate
        if (self.movecounter==0):
            self.movecounter+=1
            self.my_prev_move=self.cooperate
            return self.cooperate
        
        #Count opponents defects in a row
        if (self.opponent_move==True):
            self.opponent_defects += 1
        if (self.opponent_move==False):
            self.opponent_defects=0
            
        #Last move is always defect
        if (self.number_of_iterations != []):
            if ((self.movecounter+1)==self.number_of_iterations):
                self.my_prev_move=self.defect #Wrong data protection
                return self.defect
            
        #Anti-always-defect-loop protection
        if ((self.opponent_defects % 11)==0): #Every 11 opponent DEF in a row return COOP
            self.movecounter += 1
            self.my_prev_move=self.cooperate
            return self.cooperate      
              
        #Tit-for-tat
        self.movecounter += 1
        self.my_prev_move=self.opponent_move
        return self.opponent_move
        
        
        
