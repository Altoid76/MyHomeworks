import sys
terminal=["x","y","z","0","1","2","3","4","5","6","7","8","9","=",";","while","(",")","let","=","do","else", 
          "+","-","*",">"]
symbol=["S","F","L","E","A","C","H","B","V","N","G","D"]
dict={}
test_text=[]
stack=[]
state=0
num_of_change=0
def parsetable(filename):
    with open(filename,"r") as a:
        for line in a.readlines():
            line=line.strip("\n")
            pairs=line.split(" ")
            for pair in pairs:
                key_value=pair.split(":")
                key=key_value[0].split(",")
                value=key_value[1]
                dict[(key[0],key[1])]=value
    return
def parseinput(filename):
    counter=0
    with open(filename,"r") as text:
        line=text.read()
        line=line.replace("\n",'').replace(" ",'').replace('\t','')
        while counter<len(line):
          word=line[counter]
          if word in terminal:
            test_text.append(word)
          else:
            if word=="l":
                if line[counter:counter+3]=="let":
                  test_text.append("let");
                  counter+=2
                else:
                  print("ERROR_INVALID_SYMBOL")
                  state=1
                  return
            elif word=="w":
                if line[counter:counter+5]=="while":
                  test_text.append("while");
                  counter+=4
                else:
                  print("ERROR_INVALID_SYMBOL")
                  state=1
                  return
            elif word=="d":
                if line[counter:counter+2]=="do":
                  test_text.append("do");
                  counter+=1
                else:
                  print("ERROR_INVALID_SYMBOL")
                  state=1
                  return
            elif word=="e":
                if line[counter:counter+4]=="else":
                  test_text.append("else");
                  counter+=3
                else:
                    print("ERROR_INVALID_SYMBOL")
                    state=1
                    return
            else:
              print("ERROR_INVALID_SYMBOL")
              state=1
              break
          counter+=1
        test_text.append("$")
        return
def initializestack():
    stack.append("S")
    stack.append("$")
    return
def parsestring(text,stack):
    while len(test_text)>0 and len(stack)>0:
      input_symbol=test_text[0]
      top_symbol=stack[0]
      print("{}   {}".format(''.join(test_text),''.join(stack)))
      if input_symbol==top_symbol=="$":#accept case
        print("Accepted")
        return
      elif top_symbol in terminal or top_symbol=="$":#case when top_symbol on the stack is a terminal
        if top_symbol==input_symbol:
          stack.pop(0)
          test_text.remove(input_symbol)
        else:#when topsymbol not equals to inputsymbol
          identifyterminalnotmatched(input_symbol,top_symbol)#check what the grammar expect to accept the parsing string
          print("Rejected")
          return
      elif top_symbol in symbol and (top_symbol,input_symbol) in dict.keys():#case when top_symbol is a variable and (top_symbol,input_symbol) has entry in parsetable
        stack.remove(top_symbol)
        count=0
        position=0
        entry=dict[(top_symbol,input_symbol)]
        #inserting content into stack in reverse order
        while count<len(entry):
          word=entry[count]
          if word in terminal or word in symbol:
            stack.insert(position,word)
            count+=1
          else:
            if word=="l":
                if entry[count:count+3]=="let":
                  stack.insert(position,"let")
                  count+=3
            elif word=="w":
                if entry[count:count+5]=="while":
                  stack.insert(position,"while")
                  count+=5
            elif word=="d":
                if entry[count:count+2]=="do":
                  stack.insert(position,"do")
                  count+=2  
            elif word=="e":
                if entry[count:count+4]=="else":
                  stack.insert(position,"else")
                  count+=4
                elif entry=="epsilon":
                  count+=7
          position+=1
      else:
        identifynomatchedentry(input_symbol,top_symbol)
        print("Rejected")
        return
def identifynomatchedentry(input_symbol,top_symbol):
  if len(sys.argv)==2:
    return
  elif len(sys.argv)==3:
    if sys.argv[2]=="error":
      variable=[]
      for key in dict.keys():
        if key[0]==top_symbol:
          variable.append(key[1])
      print("Got:{}  Expected:{}".format(input_symbol," ".join(variable)))
      Give_suggestion_variable(input_symbol,top_symbol,variable)
    else:
      return
    
def identifyterminalnotmatched(input_symbol,top_symbol):
  if len(sys.argv)==2:
    return
  elif len(sys.argv)==3:
    if sys.argv[2]=="error":
      print("Got:{}  Expected:{}".format(input_symbol,top_symbol))
      Give_suggestion_terminal(input_symbol,top_symbol)
    else:
      return
def Give_suggestion_terminal(input_symbol,top_symbol):
  if test_text[1]==top_symbol and input_symbol!="$" and top_symbol!="$":
    A=input("Delete? {}".format(input_symbol))
    if A=="yes":
      test_text.pop(0)
      return
    elif A=="no":
      return
  elif input_symbol=="$" and top_symbol!="$":
    A=input("Add? {}".format(top_symbol))
    if A=="yes":
      test_text.append(top_symbol)
      return
    elif A=="no":
      return
def Give_suggestion_variable(input_symbol,top_symbol,variables):
  pass
if __name__ == "__main__":
    parsetable("parsetable.txt")
    parseinput(sys.argv[1])
    if state==1:
      sys.exit(0)
    else:
      initializestack()
      parsestring(test_text,stack)
        
                
        
        
    
