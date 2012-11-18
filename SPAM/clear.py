import os
def clear(path_to_dir):
    counter1 = 0
    counter2 = 0
    for file_name in os.listdir(path_to_dir):
        try:
            a = open(path_to_dir+file_name, 'r', encoding = 'utf-8')
            b= a.read()
            a.close()
        except UnicodeDecodeError:
            a.close()
            try:
                counter1 += 1
                print("Try remove")
                os.remove(path_to_dir+file_name)
                print(file_name)
            except WindowsError:
                counter2 +=1
                print("MOTHERFUCKER!")
    print(counter1, counter2)
                
