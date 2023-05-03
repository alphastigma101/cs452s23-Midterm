package edu.sou.cs452.jlox;

class Return extends RuntimeException {
  final Object value;
  
  Return(Object value) {
    super(null, null, false, false);
    this.value = value;
  }
}