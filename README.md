# cs452s23-Midterm
- You need to install maven. On Ubuntu it is **sudo apt-get install maven**
- If you're using another linux distro, then you need to figure out how to install maven on it 
- Execute this command: **mvn archetype:generate -DgroupId=edu.sou.cs452.jlox -DartifactId=jlox -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false**
- Then you should be able to execute the code by issuing this: **java -cp target/Lab5-1.0-SNAPSHOT.jar edu.sou.cs452.Lab5.Lox**
- Run this command to compile and run the program: **java -cp target/jlox-1.0-SNAPSHOT.jar edu.sou.cs452.jlox.GenerateAst**
We can compile the program by executing mvn package within the Lab5 directory.

We can run the program with java -cp target/Lab5-1.0-SNAPSHOT.jar edu.sou.cs452.Lab5.Lox
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