##########################################
#Python 3 ONLY
##########################################
def shortphrase(shortphrase):
        shortphrase = shortphrase.translate(str.maketrans('.',' '))
        tokens = shortphrase.lower().split()
        return tokens
