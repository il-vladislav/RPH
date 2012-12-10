def sign(x):
    """return -1 for negative and 1 for positive numbers or 0"""
    if x>=0:
        return(1)
    if x<0:
        return(-1)

class Solver:
    def __init__(self,maze):
        """abstract solver implements some methods common for solvers"""
        self.path = []
        self.maze = maze
        self.final_reached = False # is the final position reached?

    def clear_path(self):
        """empty path, delete the path already found"""
        self.path = []
        

class DummySolver(Solver):
    def __init__(self,maze):
        """simplistic solver finds a path regardless the walls and others"""
        Solver.__init__(self,maze)

    def compose_path(self):
        """simplistic solver just goes directly from start to the target over the walls"""
        # increments in row direction
        dr = range(self.maze.startpos[0], self.maze.finalpos[0], \
                   sign(self.maze.finalpos[0] - self.maze.startpos[0]))
        # increments in columne direction
        dc = range(self.maze.startpos[1], self.maze.finalpos[1], \
                   sign(self.maze.finalpos[1] - self.maze.startpos[1]))

        # append the final position
        dr.append(self.maze.finalpos[0])
        dc.append(self.maze.finalpos[1])
        # first go in row direction
        for index_row in dr:
            self.path.append((index_row, dc[0]))

        # then along the columns
        for index_column in dc[1:]:
            self.path.append((index_row, index_column))
            
        # then, we are done, actually
        self.final_reached = True

        return(True)
                
