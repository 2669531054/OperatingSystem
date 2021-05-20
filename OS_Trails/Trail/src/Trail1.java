import java.util.*;

class Student
{
    int age = 0;
    String name = "zyc";
}

public class Trail1
{
    public static void main(String[] args)
    {
        LinkedList<Student> students = new LinkedList<>();
        Student student = new Student();
        students.add(student);
        Student tempStudent = students.get(0);
        tempStudent.name = "tzx";
        System.out.println(students.get(0).name);
    }
}
