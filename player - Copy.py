import random
class MyPlayer:
    """tit for tat with forgiveness"""
    def __init__(self,payoff_matrix, number_of_iterations=0):
        self.payoff_matrix=payoff_matrix
        self.number_of_iterations=number_of_iterations
        self.badmoves=0
        self.movecounter=0 #for future
        
    def record_opponents_move(self,opponent_move):
        self.opponent_move=opponent_move
        
    def move(self):
        #Prvni tah je Cooperate
        if (self.movecounter==0):
            self.movecounter=self.movecounter+1
            return False
        #Scitame, kolik opponent hral DEFECT po sobe
        if (self.opponent_move==True):
            self.badmoves=self.badmoves+1
        if (self.opponent_move==False):
            self.badmoves=0
        #Anticyklacni DEFECT
        if (self.badmoves==6):
            self.movecounter=self.movecounter+1
            return False
        #Tii-for-tat
        self.movecounter=self.movecounter+1
        return self.opponent_move
        
