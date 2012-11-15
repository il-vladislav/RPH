def add_slash(path):
    """
    Inputs: path to dir
    Outputs: path to dir with slash
    Effects: Check if path to dir with slash or not, add slash
    """
    if path.endswith("/"): return path
    return path + "/"
