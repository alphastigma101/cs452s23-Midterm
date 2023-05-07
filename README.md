# cs452s23-Midterm
- You need to install maven. On Ubuntu it is **sudo apt-get install maven**
- If you're using another linux distro, then you need to figure out how to install maven on it 
- Execute this command to create a **virtual enivornment (JVM)**: **mvn archetype:generate -DgroupId=edu.sou.cs452.jlox -DartifactId=jlox -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false**
- Then you should be able to execute the code by issuing this because Lox has a main declared as static and anything with a main will be compiled: **java -cp target/jlox-1.0-SNAPSHOT.jar edu.sou.cs452.jlox.Lox**
- Run this command to compile and run the program. It will generate the Stmt.java and the Expr.java: **java -cp target/jlox-1.0-SNAPSHOT.jar edu.sou.cs452.jlox.GenerateAst**


# The Objectives to accomplish for this midterm:
- Create the file called TestFunction.lox. 
- Review the mechanics of how to use .lox, you can use **var x = input()**
- The main goal of this project is to create functions for the lox language using java. You can test your .lox implementation by doing this: **java -cp target/jlox-1.0-SNAPSHOT.jar edu.sou.cs452.jlox.Lox TestFunction.lox**
-  **User Input**: Implement functionality that allows tLox programs to accept input from standard input. One idea is to mimic the Python built-in function input.
- **Type conversion**: Implement a way of converting strings to numeric values. One idea is to mimic the built-in Python function int.
- **Lists**: Add support for array-like lists in the Lox language, including list creation, element retrieval, and modification. Lists should be able to store any valid Lox data type, including other lists, allowing for nested lists. Implement basic list manipulation functions such as adding, removing, and accessing elements at a specific index.
- **Prototypes**: Implement prototypal inheritance in JLox. Remove the distinction between objects and classes, and treat a class definition simply as a object in the environment with the name of the class that contains no fields, i.e. the initializer has not run. Add a .proto keyword for set expressions that dynamically update the superclass of an object. The superclass can be either another object or a class itself.
- **Static bounds checking**: Create an abstract interpretation-based bounds checker for lists. This checker should be able to detect and report potential out-of-bounds access to list elements at **compile time**, *helping to prevent runtime errors*. It should be sound with respect to the safety of the program, **meaning that it should never report that a program is safe that might produce an out-of-bounds error at runtime. It should be complete enough to allow useful programs to be run without reporting an egregious number false positives, meaning that it should not report that a program is unsafe when it is actually safe.**

# Static analysis of array bounds checking
- Source: **https://www.usenix.org/legacy/publications/library/proceedings/sec02/full_papers/lhee/lhee_html/node10.html**
- is a technique that detects possible buffer overflow in the vulnerable C library functions. A string buffer is modeled as a pair of integer ranges (lower bound, upper bound) for its allocated size and its current length. A set of integer constraints is predefined for a set of string operations (e.g. character array declaration, vulnerable C library functions and assignment statements involving them). Using those integer constraint, the technique analyzes the source code by checking each string buffer to see whether its inferred allocated size is at least as large as its inferred maximum length. **But it is not only limited to c language.**
- uses semantic comments, called annotations, provided by programmers to detect possible buffer overflow. For example, annotations for strcpy() contain an assertion that the destination buffer has been allocated to hold at least as many characters as are readable in the source buffer. This technique protects any annotated functions whereas the integer range analysis only protects C library functions.

- Generally, a pure compile-time analysis like the above can produce many false alarms due to the lack of run time information. For example, gets() reads its input string from stdin so the size of the string is not known at compile time. **For such a case a warning is issued as a possible buffer overflow.** In fact, all the legitimate copy operations that accept their strings from unknown sources (such as a command line argument or an I/O channel) are flagged as possible buffer overflows (since they are indeed vulnerable). Without further action, those vulnerabilities are identified but still open to attack. 

- Source: **https://stackoverflow.com/questions/12924533/java-static-methods-variables-resolved-during-compile-time-loaded-during-run-ti**
- Resolving a method/variable means deciding exactly which method/variable is called. which there is a java file called **Resolver.java**. 
- **For instance methods for example this is done at runtime, which results in the ability of a subclass to override a superclass's methods (polymorphism).**
- **Static methods however cannot be overridden and are resolved at compile time.**



# Summary Static analysis of array bounds checking
- create a file or files that only have static declarations. 

- Prototypes
  - The goal of this part of the midterm is implement a form of prototypal inheritance in JLox. In our version of prototypes,

We remove the distinction between objects and classes, and treat a class definition simply as a object in the environment with the name of the class that contains no fields, i.e. the initialize has not run.
We will **add a .proto keyword** for set expressions that dynamically update the superclass of an object. The superclass can be either another object or a class itself.
Here are a few example programs that illustrate the semantics of this design.

# Example 1: Setting a field on a Class prototype
```sh
  // Expected output: green\n

  class Object {}
  var o = Object();

  // Updates the prototype of the Object class.
  // All descendants of Object will now have color field set to green.
  // Note that this is set after o is created.
  Object.color = "green";
  print o.color;
```
- Example 2: Overwriting a field in superclass but inheriting method.
```sh
  // Expected output: 314.0\n or 314\n

  class Object {}
  class Circle {
  init (radius) { this.radius = radius; }
  area() { return 3.14 * this.radius * this.radius; }
  var o1 = Object();
  var c = Circle(4);
  o1.radius = 10;
  o1.proto = c;
  print o1.area();
```
- Example 3: Inheriting Data directly from another object
```sh
  // Expected output: MyObject\n

  class Object {}
  var o1 = Object();
  o1.name = "MyObject";
  var o2 = Object();
  o2.proto = o1;
  print o2.name;
```

# Prerequisites:
**Before completing this lab you should have completed your normal JLox interpreter.** All of the components through chapter 13 of the book should be working.

# Instructions
The code snippets in these instructions were taken from my implementation. Some adaptation may be necessary.

# Part 1: Scanner and Parser Changes
We are going to treat .proto as a special case of set expressions. The set expression o.proto = c; will set the superclass of o to c. The variable c must map to a LoxClass object. The distinction between LoxClass and LoxInstance will be removed, so c may refer to a class definition or an object of that class.

- Add a PROTO token type.

- Put the proto keyword into the Scanner:
```sh
  keywords.put("proto", PROTO);
  // Add a special case in the Parser for field accesses that handles the PROTO token.
  Token name;
  if (check(IDENTIFIER)) {
    name = consume(IDENTIFIER, "Expect property name after '.'.");
  } else if (check(PROTO)) {
    name = consume(PROTO, "Expect proto after '.'.");
  } else {
    throw error(peek(), "Expect property or proto after dot.");
  }
```
# Part 2: Combining LoxClass and LoxInstance
Make the superklass field of LoxClass private instead of final. It cannot be final because we are going to allow programs to change the superclass of an object at runtime.

**Add a setter method for the superklass field of LoxClass so that it can be updated by the interpreter.** 
Previously this would only be updated at the time of declaration using class inheritance in the declaration with <.
```sh
public void setSuperklass(LoxClass superklass) {
  this.superklass = superklass;
}
```
- Move the fields HashMap from LoxInstance to LoxClass.

Have a constructor that sets the **superclass** of a LoxClass
```sh
LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
  this.name = name;
  this.methods = methods;
  this.superklass = superklass;
}
```
**The call method in LoxClass should return a LoxClass instead of a LoxInstance.** The superclass field of the new object should be set to the class that created the object.
```sh
@Override
public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
  // Construct an object from a class.
  // - Does not take the class name (unused)
  // - Sets superklass to this class
  // - Empty methods (method lookup delegated to this class)
  LoxClass instance = new LoxClass("", this, new HashMap<>());
```
LoxFunctions should bind LoxClass objects instead of LoxInstance objects.
Move the set method from LoxInstance into LoxClass.

Move the **get method** from LoxInstance into **LoxClass.** 
**If the current object does not contain the field or method, then look in the superclass.**
**It is important to perform this check after looking for both fields and methods.**
```sh
// When looking up a method, return that method enclosed
// over the current instance (this)
LoxFunction method = findMethod(name.getLexeme());
if (method != null) return method.bind(this);

  // We don't have field or method on this object with that name.
  // Check the parent.
  if (superklass != null && superklass.get(name) != null) {
    return superklass.get(name);
  }
}
```
When you are finished the LoxInstance class can be removed (delete LoxInstance.java). Now we need to make some changes to the interpreter.
Every instanceof check for LoxInstance objects should be rewritten to be a check for LoxClass objects.

**In visitGetExpr** when you create a LoxInstance object create a LoxClass instead.
```sh
  // Try looking up the field/method on the object
  if (object instanceof LoxClass) {
    LoxClass instance = (LoxClass) object;
    LiteralValue result = instance.get(expr.getName());
    return result;
  }
```
**In visitSetExpr check to see if the field name is a PROTO token type.** If so, then set the superclass of the LoxClass to the value. The behavior for other fields should be unchanged.
```sh
  // Prototype-based inheritance by setting super directly.
  if (expr.getName().getType() == TokenType.PROTO) { ((LoxClass) object).setSuperklass((LoxClass) value); } 
  else { ((LoxClass) object).set(expr.getName(), value); }
```

# Java Types:
- Source: https://web.mit.edu/6.005/www/fa15/classes/01-static-checking/
- The most important semantic difference between the Python and Java code above is the declaration of the variable n, which specifies its type: int. A type is a set of values, along with operations that can be performed on those values.

**Java has several primitive types, among them:**

  *  int (for integers like 5 and -200, but limited to the range ± 2^31, or roughly ± 2 billion)
  *  long (for larger integers up to ± 2^63)
  *  boolean (for true or false)
  *  double (for floating-point numbers, which represent a subset of the real numbers)
  *  char (for single characters like 'A' and '$')
**Java also has Wrapper Types or you can say Object Types which the first letter is capitalized:**
  * **Double**:
  * **Integer**:
  * **String**:
  * **Char**: 
  * **Bool**:  
  * **String**: represents a sequence of characters, like a Python string.
  * **BigInteger**: represents an integer of arbitrary size, so it acts like a Python number.

**Operations are functions that take inputs and produce outputs (and sometimes change the values themselves). The syntax for operations varies, but we still think of them as functions no matter how they’re written. Here are three different syntaxes for an operation in Python or Java:**
  * As an infix, prefix, or postfix operator. For example, a + b invokes the operation + : int × int → int.
  * As a method of an object. For example, bigint1.add(bigint2) calls the operation add: BigInteger × BigInteger → BigInteger.
  * As a function. For example, Math.sin(theta) calls the operation sin: double → double. Here, Math is not an object. It’s the class that contains the sin function.
  * Some operations are overloaded in the sense that the same operation name is used for different types. The arithmetic operators +, -, *, / are heavily overloaded for the numeric primitive types in Java. Methods can also be overloaded. Most programming languages have some degree of overloading.


# Static Typing
- Java is a statically-typed language. **The types of all variables are known at compile time (before the program runs), and the compiler can therefore deduce the types of all expressions as well.** If a and b are declared as ints, then the compiler concludes that a+b is also an int. The Eclipse environment does this while you’re writing the code, in fact, so you find out about many errors while you’re still typing.

- **Static typing** *is a particular kind of static checking, which means checking for bugs at compile time.* **Bugs are the bane of programming.** Many of the ideas in this course are aimed at eliminating bugs from your code, and static checking is the first idea that we’ve seen for this. Static typing prevents a large class of bugs from infecting your program: to be precise, bugs caused by applying an operation to the wrong types of arguments. If you write a broken line of code like:
```
"5" * "6"
```

# Test suites for JVM:
**Using jupiter to make test cases**
  - https://stackoverflow.com/questions/45175418/import-junit-jupiter-api-not-found
  - https://junit.org/junit5/docs/current/user-guide/
  - https://stackoverflow.com/questions/72467220/junit-assertions-assertthrows-works-but-expectedexception-doesnt
**Using junit to make test cases**
- https://www.javatpoint.com/junit-test-case-example-in-java