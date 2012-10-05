import random
class MyPlayer:
    """
i build a many buildings
well some of them have failed"""
    def __init__(self,payoff_matrix, number_of_iterations=0):
        #vstupni parametry 
        self.payoff_matrix=payoff_matrix
        self.number_of_iterations=number_of_iterations
        self.opponent_move=[]

    def move(self):
        #vraci tah(pro nejjednodussiho hraca random-tah)
        i=random.choice([1,0])
        if (i==0):
            return True
        else:
            return False
 
    def record_opponents_move(self,opponent_move):
        #Tah opponenta
        self.opponent_move.append(opponent_move)

