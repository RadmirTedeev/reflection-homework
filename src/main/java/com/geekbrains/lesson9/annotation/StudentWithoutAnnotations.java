package com.geekbrains.lesson9.annotation;

@Table(title = "studentTest")
public class StudentWithoutAnnotations {

    @Column(name = "name")
    private String name;

    private int age;

    @Column(name = "score")
    private int score;

    public StudentWithoutAnnotations(String name, int age, int score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "StudentWithoutAnnotations{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", score=" + score +
                '}';
    }
}
