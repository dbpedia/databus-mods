package abc;

public class Person {

    int age;
    String name;

    Person(int age_, String name_) {
        this.age = age_;
        this.name = name_;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
