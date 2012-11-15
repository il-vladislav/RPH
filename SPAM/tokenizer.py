##########################################
#Python 3 ONLY
##########################################
def shortphrase(shortphrase):
        if type(shortphrase) == str:
                shortphrase = shortphrase.translate(str.maketrans('.',' '))
                tokens = shortphrase.lower().split()
                return tokens
        return ""
