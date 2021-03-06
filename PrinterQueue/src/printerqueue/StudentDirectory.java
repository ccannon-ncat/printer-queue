/*
 * Copyright (C) 2017 North Carolina A&T State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package printerqueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CCannon
 */
public class StudentDirectory {
    private HashMap<String, Student> directory;
    public static final String configFileName = System.getenv("APPDATA") + "\\printerQueue\\studentDirectoryConfigFile.txt";
    
    public StudentDirectory() {
        directory = new HashMap();
    }
    
    public void saveStudentDirectory() {
        try {
            PrintWriter writer = new PrintWriter(new File(configFileName));
            
            writer.print(this.toString());
            
            writer.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to open " + configFileName + " for writing.");
            Logger.getLogger(StudentDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadStudentDirectory() {
        try {
            Scanner reader = new Scanner(new File(configFileName));
            while(reader.hasNext()) {
                String[] studentString = reader.nextLine().split(",");
                
                if(!directory.containsKey(studentString[2].trim())){
                    Student newStudent = new Student(studentString[0].trim(), studentString[1].trim(), studentString[2].trim(), studentString[3].trim(), studentString[4].trim());
                    directory.put(newStudent.getStudentID(), newStudent);
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to open " + configFileName + " for reading.");
            Logger.getLogger(StudentDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean containsStudent(String studentID) {
        return directory.containsKey(studentID);
    }
    
    public Student getStudent(String studentID) {
        return directory.get(studentID);
    }
    
    public void putStudent(String studentID, Student student) {
        directory.put(studentID, student);
        this.saveStudentDirectory();
    }
    
    public Collection<Student> getStudents() {
        return directory.values();
    }
    
    public String toString() {
        Collection<Student> outputDir = directory.values();
        
        String returnString = "";
        
        for(Student student : outputDir) {
            returnString += student.toString() + System.lineSeparator();
        }
        
        return returnString;
    }
}
