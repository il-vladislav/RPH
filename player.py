import random
class MyPlayer:
    """good"""
    def __init__(self,payoff_matrix, number_of_iterations=0):
        self.payoff_matrix=payoff_matrix
        self.number_of_iterations=number_of_iterations
        self.opponent_move=[]
        self.next_move=self.opponent_move
        self.movecounter=0
        self.badmoves=0
        
    def record_opponents_move(self,opponent_move):
        self.next_move=opponent_move
        
    def move(self):
        if (self.movecounter==0):
            self.movecounter=1
            return True
        #bad_opponent_move_counter()
        if (self.badmoves==6):
            return True		
        return self.next_move
        
    def bad_opponent_move_counter(self):
        if (self.next_move==True):
            self.badmoves=self.badmoves+1
        if (self.next_move==False):
            self.badmoves=0
        
