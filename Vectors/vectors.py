class MyVector:
	def __init__(self,vector=None): 
		self.vector=vector if vector else [] #Some tutorial (No human factor errors)

	def get_vector(self):
		return(self.vector)		
	
        #Dot product	
	def __mul__(self,other):
		dot=sum(p*q for p,q in zip(self.vector, other.vector))
		return(dot)
