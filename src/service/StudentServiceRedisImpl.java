package service;

import model.Student;
import redis.clients.jedis.Jedis;
import service.exception.DuplicateEntryException;
import service.exception.NotFoundException;
import util.JedisClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StudentServiceRedisImpl {

    private static final String DB_PREFIX = "s#";

    private final Jedis client;

    public StudentServiceRedisImpl() {
        client = JedisClient.getInstance().getClient();
    }

    public void saveStudent(Student student) throws DuplicateEntryException {

        if (client.exists(DB_PREFIX + student.getNic())) {
            throw new DuplicateEntryException();
        }
        client.hset(DB_PREFIX + student.getNic(), student.toMap());
    }

    public void updateStudent(Student student) throws NotFoundException {

        if (!client.exists(DB_PREFIX + student.getNic())) {
            throw new NotFoundException();
        }
        client.hset(DB_PREFIX + student.getNic(), student.toMap());
    }

    public void deleteStudent(String nic) throws NotFoundException {
        if (!client.exists(DB_PREFIX + nic)) {
            throw new NotFoundException();
        }
        client.del(DB_PREFIX + nic);
    }

    private boolean exitsStudent(String nic) {
        return client.exists(DB_PREFIX + nic);
    }

    public Student findStudent(String nic) throws NotFoundException {
        if (!client.exists(DB_PREFIX + nic)) {
            throw new NotFoundException();
        }
        return Student.fromMap(nic.replace(DB_PREFIX, ""), client.hgetAll(DB_PREFIX + nic));
    }

    public List<Student> findAllStudents() {
        List<Student> studentList = new ArrayList<>();
        Set<String> nicList = client.keys(DB_PREFIX + "*");

        for (String nic : nicList) {
            studentList.add(Student.fromMap(nic.replace(DB_PREFIX, ""), client.hgetAll( nic)));
        }
        return studentList;
    }

    public List<Student> findStudents(String query) {
        List<Student> searchResult = new ArrayList<>();
        Set<String> nicList = client.keys(DB_PREFIX + "*");

        for (String nic : nicList) {

            if (nic.contains(query)){
                searchResult.add(Student.fromMap(nic.replace(DB_PREFIX, ""), client.hgetAll(nic)));
            }else{
                List<String> data = client.hvals(DB_PREFIX + nic);

                for (String value : data) {
                    if (value.contains(query)){
                        searchResult.add(Student.fromMap(nic.replace(DB_PREFIX, ""), client.hgetAll(nic)));
                        break;
                    }
                }
            }
        }

        return searchResult;
    }
}
