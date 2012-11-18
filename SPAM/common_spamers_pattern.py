def check_for_common_spammer_patters(self, msg, fname):               
                #######################################################################
                #Subject vars
                subject_contains_repeated_letters = False
                count_words_without_vowels = 0  
                count_words_with_two_JKQXZ = 0
                count_words_with_15_symbol = 0
                count_words_only_uppercase = 0

                #######################################################################
                #Content type vars                                                    
                content_type_text_html = False
                message_priority = False

                #######################################################################
                #Body vars
                words_without_vowels_body_counter = 0
                Number_of_HTML_opening_comment_tags = 0
                alphabetic_words_counter = 0
                count_words_with_at_lest_two_JKQXZ = 0
                count_alphabetic_words_15_long = 0
                
                words_without_vowels_proportion = 0
                words_with_at_lest_two_JKQXZ_proportion = 0
                alphabetic_words_15_long_proportion = 0
                
                from_equals_to = False
                
                two_letters = "jkqxz"
                uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        
                #######################################################################
                #Check for common spammer patters from subject header
                #######################################################################
                        
                
                #Number of words with all letters in uppercase
                if msg['Subject']:
                        for word in msg['Subject']:
                                uppercase_counter = 0
                                for letter in word:
                                        if letter in uppercase:
                                                uppercase_counter += 1
                                if uppercase_counter == len(word):
                                        count_words_only_uppercase += 1

                #Tokenize subject to string with all lower letters
                for word in tokenizer.shortphrase(msg['Subject']):
                        two_letters_counter = 0
                        for letter in word:
                                #Count 'J K Q X Z' letters in word
                                if letter in two_letters:
                                        two_letters_counter += 1
                                
                        #Is word with no vowels?     
                        if self.word_without_vowels(word): 
                                count_words_without_vowels += 1
                                
                        #Is words with at least two of letters J, K, Q, X, Z?
                        if two_letters_counter >= 2:
                                count_words_with_two_JKQXZ += 1
                                
                        #Is word with at least 15 characters
                        if len(word) >= 15:
                                count_words_with_15_symbol += 1

                        #Binary feature indicating 3 or more repeated characters
                        if re.search(r'(.)\1\1', word):
                                cont_words_with_repeat_sym = True

                #######################################################################
                #Check for common spammer patters from Content-Type header and Priority
                #######################################################################
                for word in tokenizer.shortphrase(msg['Content-Type']):
                #Binary feature indicating the content type had been set to “text/html”
                        if word == ('text/html' or 'text/html;'):
                                content_type_text_html = True

                #Binary feature indicating whether the priority had  been set to any level not None
                for word in tokenizer.shortphrase(msg['Pirority']):
                        if word != None:
                                message_priority = True

                #######################################################################
                #Check for common spammer patters from body
                #######################################################################
                JKQXZ = 0 #counter of JKQZX letters in word
                l_counter = 0 #counter of letters in word
                for word in tokenizer.shortphrase(self.get_text(msg)):
                    if self.find_alphabetic_words:
                        alphabetic_words_counter +=1
                        #Counter of alphabetic words with no vowels and at least 7 characters
                        if len(word)<7:
                                if self.word_without_vowels(word):
                                                words_without_vowels_body_counter += 1                                                
                        #Counter of alphabetic words with at least two of letters J, K, Q, X, Z
                        for letter in word:
                            l_counter +=1
                            if letter in two_letters:
                                JKQXZ += 1
                         #Counter of alphabetic words with at least 15 letters
                        count_alphabetic_words_15_long += 1
                if JKQXZ >1:
                    count_words_with_at_lest_two_JKQXZ += 1
                    
                #Proportion of alphabetic words with no vowels and at least 7 characters
                try:
                        words_without_vowels_proportion = words_without_vowels_body_counter/alphabetic_words_counter
                        self.a += words_without_vowels_proportion 
                except ZeroDivisionError:
                        self.a +=  0
                try:
                        words_with_at_lest_two_JKQXZ_proportion = count_words_with_at_lest_two_JKQXZ/alphabetic_words_counter
                        self.b += words_with_at_lest_two_JKQXZ_proportion 
                except ZeroDivisionError:
                        self.b +=  0
                    
                try:
                        alphabetic_words_15_long_proportion = count_alphabetic_words_15_long/alphabetic_words_counter
                        self.c += alphabetic_words_15_long_proportion 
                except ZeroDivisionError:
                        self.c +=  0                
                #print(words_without_vowels_proportion,words_with_at_lest_two_JKQXZ_proportion,alphabetic_words_15_long_proportion,self.truth[fname])
                
                #######################################################################
                #Check for common non-spammer patters from FROM and TO
                #######################################################################
                FROM = tokenizer.shortphrase(self.extract_email_adress_from_text(msg['From']))
                TO = tokenizer.shortphrase(self.extract_email_adress_from_text(msg['To']))
                if (FROM==TO):
                        from_equals_to = True

                #######################################################################
                #HTML
                #######################################################################
                Number_of_HTML_opening_comment_tags = self.find_in_string('<!--',' '.join(tokenizer.shortphrase((self.get_text(msg)))))
                Number_of_hyperlinks = self.find_in_string('href=',' '.join(tokenizer.shortphrase((self.get_text(msg)))))
                White_text_examples = ['3D#ff0000', 'red', '3D"#000000"', '3D"#FF0000"', '3D"blue"', '#333333', '3D"#ffffff"', '#ffff80', '3D"#0000FF"', '#000000', '3D#000000', '3D"white"', '#800000', '3D#000000', '3D#0000ff', '3D"blue"', 'red', '#ff0000', '3D"#0000FF"', '3D"blue"', '3D"blue"', '#FF0033', '3D#000000', '#0000FF', '202498', '3D"#FFFFFF"', '#999999', '3D"#000066"', '000000', '#ff6600', '3D#000000', '#ff0000', '3D"#000066"', '3D#ffffff', '3D"#33333=', '3D#FF0000', '#999999', '3D"#00FF00"', 'black', '#fd0000', '#ff6600', 'black', '3D"#000066"', '#333366', '#FFFF00', '3D=22=23FF0000=22', '#FFFFFF', '3D"#008000"', '#2e4361', '3D"#000000"', 'black', 'black', 'black', '#666666', '3D"#FFFFFF"', '#666666', '#333333', '3D#000000', 'blue', '#333333', '#000000', '3D"#FFFF00"', '3D"#CC3333"', '3D"#FFFFFF"', '#000000', '3D"#FFFFFF"', '#999999', '#ff0000', '#ff0000', '#ff0000', '3D"#000066"', '3D"#FFFFFF"', '#FF0000', '3D"#000066"', '3D#ff0000', '3D"ED1C24"', '3D"ED1C24"', '3D#000000', '#666666', '3D"#FFFFFF"', '#000000', 'Firebrick', '#FFFFFF', '#3333FF', '#33CC99', '3D=22=23990000=22', '3D=23ffffff', '#000080', '#ff0000', '#000000', '3D"#66FF00"', '#000000', '#000080', '3D"#99ffff"', '#000000', 'gray', '3D=23ffffff', '#FFFFFF', 'gray', '#ffffff', '3Dred', '3D"#FF0000"', '#000000', '#000000', '3D=22=23=', '3D"#000080"', '#0000FF', '#000080', '3D=23ffffff', '#294D7F', '3D=23ffffff', '#000080', '#FFFFFF', '#FF0000', '3D#000000', '3D"#000066"', '#ff8080', '3D"#000080"', '3D"#0033=', '#ffffff', 'Firebrick', '#FF0000', '3D"487EB3"', '3D#000000', '3D#000000', '3D"#ffffff"', '#ff0000', 'Silver', '#FF0000', '3D"#333333"', '#660000']
                soup = BeautifulSoup(self.get_text(msg))
                a = 'none'
                white_text = False
                for i in White_text_examples:
                        try:
                                a = soup.font['color']
                        except (KeyError,TypeError):
                                pass
                        
                        if a == i:
                                white_text = True
                                
                return(subject_contains_repeated_letters,
                       count_words_without_vowels,
                       count_words_with_two_JKQXZ,
                       count_words_with_15_symbol,
                       count_words_only_uppercase,
                       content_type_text_html,
                       message_priority,
                       words_without_vowels_body_counter,
                       from_equals_to,
                       white_text)

-        def generate_file_from_dict(self, fname, my_new_dict):
-                """                 
-                Inputs: path to dir, file name ('!hamers.txt' for example) and new dictionary
-                Outputs: none
-                Effects: Generate new file with dictionary. Check if file exist and then fusion two dictionaries (existing and new).
-                """
-                mfile = fname
-                if os.path.exists(mfile):
-                        mfile = open(fname,'rb')
-                        my_existing_dict = pickle.load(mfile)
-                        my_new_dict = my_new_dict.copy()
-                        my_new_dict.update(my_existing_dict)                        
-                        mfile.close()                
-                mfile = open(fname, 'wb+')
-                pickle.dump(my_new_dict, mfile)
-                mfile.close()
-
-        def read_dict_from_file(self,fname):
-                """
-                Inputs:  name of file with dictionary
-                Outputs: dictionary from file
-                Effects: read existing dictionary from file [run test() before train()]
-                """                
-                pkl_file = open(fname, 'rb')
-                my_dict = pickle.load(pkl_file)
-                pkl_file.close()
-                return my_dict
-
+                
         def check_subject(self, msg, fname):
                 """
                 Inputs: path to dir
@@ -485,13 +161,17 @@ class MyFilter:
                         self.spam_subject_list[i] = fname
                 elif (self.truth[fname] == 'OK'):
                         self.ham_subject_list[i] = fname
+                        
+                
+                
+        
+        
+
+
+        
                 
 
-         #TO DO Number of words with non-English characters, special characters such as punctuation, or digits at beginning or middle of word         
-        """def word_with_digits_checker(self, word):
-                begin_searcher = re.compile(r'[0-9]+[\w\-]')
-                middle_searcher = re.compile(r'[\w\-]+[0-9]+[\w\-]')
-                both_checker = re.compile(r'[0-9]+[\w\-]+[0-9]+[\w\-]')"""      
+          
                 
                 

