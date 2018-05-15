package project_sem_2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.StrictMath.round;

public class GradeCalculator {
    private JPanel mainWindow;
    private JLabel programTitle;
    private JLabel currentDescription;
    private JPanel mainPane;
    private JPanel actionPane;
    private JPanel formPane;
    private JButton nextStepBTN;
    private JButton resetBTN;
    private JPanel singleInputPane;
    private JFormattedTextField singleInputField;
    private JPanel studentsInputPane;
    private JPanel studentsFormPane;
    private JFormattedTextField nameInputField;
    private JFormattedTextField profStudGradeInputField;
    private JButton addRowBTN;
    private JScrollPane studentTablePane;
    private JTable studentTable;
    private JPanel gradeProStudentPane;
    private JLabel evalueterNameLabel;
    private JFormattedTextField evalueterGradeInput;
    private JPanel intermediateResultPane;
    private JLabel intermediateResultLable;
    private JScrollPane finalResultScrollPane;
    private JTable finalResultTable;
    private JPanel authorPane;
    private JLabel authorLabel;
    private JPanel finalResultPane;

    private DefaultTableModel studentsInputModel;
    private double teacherGrade = -1.0;
    private double zooming = -1.0;
    private List<String> studentNames = new ArrayList<>();
    private Map<String, Double> studentNameWithTeacherGrade = new HashMap<>();
    private Map<String, Double> toTest = new HashMap<>();
    private String currentStudent;
    private List<String> tmpNames = new ArrayList<>();
    private List<String> doneStudentNames = new ArrayList<>();
    private List<Double> currentStudentEvaluations = new ArrayList<>();
    private Map<String, List<Double>> studentNameWithStudentsGrades = new HashMap<>();
    private Map<String, Double> geometrical_mean_per_student = new HashMap<>();
    private boolean showIntermediateResults = false;
    private double geometrical_sum;
    private double group_median = 1;
    private boolean done = false;
    private boolean readyToTest = false;

    public GradeCalculator() {
        addRowBTN.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                add_student_to_table();
            }
        });
        nextStepBTN.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                workflow();
            }
        });
        resetBTN.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        nameInputField.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                profStudGradeInputField.requestFocus();
            }
        });
        singleInputField.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                workflow();
            }
        });
        profStudGradeInputField.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                add_student_to_table();
            }
        });
        evalueterGradeInput.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                workflow();
            }
        });
    }

    public static void main(String[] args) {
        JFrame counter = new JFrame("Grade Calculator");
        counter.setContentPane(new GradeCalculator().mainWindow);
        counter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        counter.setSize(650, 350);
        counter.setVisible(true);
    }

    private void workflow(){
        if (teacherGrade == -1.0){
            get_teacher_grade();
        }
        else if (zooming == -1.0) {
            get_zooming();
        }
        else if (studentNameWithTeacherGrade.isEmpty()){
            student_to_map();
        }
        else if (readyToTest){
            test();
        }
        else if (done){
            calculate_final_results();
        }
        else if (!showIntermediateResults){
            student_evaluation();
        }
        else {
            gradeProStudentPane.setVisible(true);
            currentDescription.setVisible(true);
            intermediateResultPane.setVisible(false);
            showIntermediateResults = false;
            evalueterGradeInput.requestFocusInWindow();
        }
    }

    private void get_teacher_grade(){
        try {
            int tmp = Integer.parseInt(singleInputField.getText());
            if (tmp > 0 && tmp <= 100) {
                teacherGrade = tmp/100.0;
                currentDescription.setText("enter zooming parameter");
                singleInputField.setText("");
                singleInputField.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(null, "Enter a grade in points between 1 and 100");
                singleInputField.setText("");
                singleInputField.requestFocus();
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Enter a valid grade");
            singleInputField.setText("");
            singleInputField.requestFocus();
        }
    }
    private void get_zooming(){
        try {
            double tmp = Double.parseDouble(singleInputField.getText());
            if (tmp >= 0) {
                zooming = tmp;
                currentDescription.setText("enter zooming parameter");
                singleInputField.setText("");
                student_input_table();
                studentsInputPane.setVisible(true);
                singleInputPane.setVisible(false);
                nameInputField.requestFocusInWindow();
                currentDescription.setText("Add students and professor evaluation (the sum should be 100)");
            } else {
                JOptionPane.showMessageDialog(null, "Enter a zooming parameter >= 0");
                singleInputField.setText("");
                singleInputField.requestFocus();
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Enter a valid number");
            singleInputField.setText("");
            singleInputField.requestFocus();
        }
    }
    private void add_student_to_table(){
        String name = nameInputField.getText();
        try {
            if (!name.equals("")) {
                if(!studentNames.contains(name)){
                    studentsInputModel.addRow(new Object[]{name, Integer.parseInt(profStudGradeInputField.getText())});
                    studentTable.updateUI();
                    studentNames.add(name);
                    nameInputField.setText("");
                    profStudGradeInputField.setText("");
                    nameInputField.requestFocusInWindow();
                }
                else {
                    JOptionPane.showMessageDialog(null, "enter a unique name");
                    nameInputField.setText("");
                    nameInputField.requestFocus();
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "enter a name");
                nameInputField.setText("");
                nameInputField.requestFocus();
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "enter a valid number");
            profStudGradeInputField.requestFocusInWindow();
            profStudGradeInputField.setText("");

        }

    }
    private void student_to_map() {
        DefaultTableModel dtm = (DefaultTableModel) studentTable.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        double tmp = 0;
        for (int i = 0; i < nRow; i++) {
            tmp += Double.parseDouble(dtm.getValueAt(i, 1).toString());
        }
        if (tmp != 100.0){
            JOptionPane.showMessageDialog(null, "the sum of teacher grades should be equal to 100");
            evalueterGradeInput.requestFocusInWindow();
        }
        else {
            studentNames = new ArrayList<>();
            for (int i = 0; i < nRow; i++) {
                double teacher_grade = Double.parseDouble(dtm.getValueAt(i, 1).toString()) / 100;
                String name = dtm.getValueAt(i, 0).toString();
                studentNames.add(name);
                studentNameWithTeacherGrade.put(name, teacher_grade);
            }
            evalueterNameLabel.setText(studentNames.get(1) + " evlauation");
            currentDescription.setText("Evaluation of " + studentNames.get(0));
            studentsInputPane.setVisible(false);
            gradeProStudentPane.setVisible(true);
            evalueterGradeInput.requestFocusInWindow();
        }
    }
    private void student_input_table(){
        studentsInputModel = new DefaultTableModel();
        studentsInputModel.addColumn("Student Name");
        studentsInputModel.addColumn("Professor-Student evaluation");
        studentTable.setModel(studentsInputModel);
        studentTablePane.setViewportView(studentTable);
    }
    private void student_evaluation(){
        if (tmpNames.isEmpty()){
            currentStudent = studentNames.get(0);
            tmpNames.addAll(studentNames);
            tmpNames.addAll(doneStudentNames);
            tmpNames.remove(currentStudent);
        }
        try {
            double tmp_note = Double.parseDouble(evalueterGradeInput.getText());
            if (tmp_note >= 0){
                currentStudentEvaluations.add(tmp_note);

                try {
                    tmpNames.remove(0);
                    evalueterNameLabel.setText(tmpNames.get(0) + " evlauation");
                }catch (Exception e){}
            }
            else {
                JOptionPane.showMessageDialog(null, "The grade should be positive");

            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Add valid number");
        }
        if (tmpNames.isEmpty()){
            intermediateResultPane.setVisible(true);
            studentNameWithStudentsGrades.put(currentStudent, currentStudentEvaluations);
            get_geometrical_mean();
            geometrical_mean_per_student.put(currentStudent, geometrical_sum);
            currentStudentEvaluations = new ArrayList<>();
            doneStudentNames.add(currentStudent);

            showIntermediateResults = true;
            gradeProStudentPane.setVisible(false);
            currentDescription.setVisible(false);
            intermediateResultPane.setVisible(true);
            nextStepBTN.requestFocus();
            intermediateResultLable.setText(currentStudent + "'s intermediate result: " + geometrical_sum);

            try {
                studentNames.remove(0);
                currentDescription.setText("Evaluation of " + studentNames.get(0));
                currentStudent = studentNames.get(0);
                evalueterNameLabel.setText(studentNames.get(1) + " evlauation");
            }catch (Exception e){}


        }
        evalueterGradeInput.requestFocusInWindow();
        evalueterGradeInput.setText("");
        if (studentNameWithStudentsGrades.size() == studentNameWithTeacherGrade.size()){
            done = true;
            nextStepBTN.setText("show results");
            studentsInputPane.setVisible(false);

        }


    }
    private void get_geometrical_mean(){
        List<Double> list_of_grades = currentStudentEvaluations;
        double sum = 1;
        for (Double list_of_grade : list_of_grades) {
            sum = list_of_grade * sum;
        }
        geometrical_sum = round(Math.pow(sum, 1.0 / list_of_grades.size()));
    }
    private void calculate_final_results(){
        intermediateResultPane.setVisible(false);
        for (Map.Entry<String, Double> entry : geometrical_mean_per_student.entrySet()){
            double person_median = entry.getValue();
            group_median = group_median * Math.pow(person_median, studentNameWithTeacherGrade.get(entry.getKey()));
        }
        finalResultPane.setVisible(true);
        DefaultTableModel resultModel = new DefaultTableModel();
        resultModel.addColumn("Student Name");
        resultModel.addColumn("result (points)");
        resultModel.addColumn("result (german)");
        for (Map.Entry<String, Double> entry : geometrical_mean_per_student.entrySet()){
            double points = (Math.pow(entry.getValue(), zooming) * teacherGrade)/((Math.pow(entry.getValue(), zooming) * teacherGrade) + (Math.pow(group_median, zooming)*(1 - teacherGrade)));
            toTest.put(entry.getKey(), points);
            points *= 100;
            points = Math.round(points);
            double german_grade;
            if (points < 50) german_grade = 5.0;
            else if (points <= 55) german_grade = 4.0;
            else if (points <= 60) german_grade = 3.7;
            else if (points <= 65) german_grade = 3.3;
            else if (points <= 70) german_grade = 3.0;
            else if (points <= 75) german_grade = 2.7;
            else if (points <= 80) german_grade = 2.3;
            else if (points <= 85) german_grade = 2.0;
            else if (points <= 90) german_grade = 1.7;
            else if (points <= 95) german_grade = 1.3;
            else german_grade = 1.0;
            resultModel.addRow(new Object[]{entry.getKey(), (int)points, german_grade});
        }
        finalResultTable.setModel(resultModel);
        finalResultScrollPane.setViewportView(finalResultTable);
        nextStepBTN.setText("run test");
        readyToTest = true;

    }
    private void test(){
        double test_left = 1;
        for (Map.Entry<String, Double> entry : toTest.entrySet()){
            test_left = test_left * Math.pow((entry.getValue()/(1-entry.getValue())), studentNameWithTeacherGrade.get(entry.getKey()));
        }
        test_left *= 10000;
        test_left = Math.round(test_left);
        double test_right = teacherGrade/(1-teacherGrade);
        test_right *= 10000;
        test_right = Math.round(test_right);
        test_right = test_right/10000;
        test_left = test_left/10000;
        String test_result = "the left site of test = " + test_left + "; \nthe right site of test = " + test_right;
        if (test_left == test_right){
            test_result += "\ntest was successful";
        }
        JOptionPane.showMessageDialog(null, test_result);
    }
    private void reset(){
        teacherGrade = -1.0;
        zooming = -1.0;
        studentNames = new ArrayList<>();
        studentNameWithTeacherGrade = new HashMap<String, Double>();
        toTest = new HashMap<>();
        tmpNames = new ArrayList<>();
        doneStudentNames = new ArrayList<>();
        currentStudentEvaluations = new ArrayList<>();
        studentNameWithStudentsGrades = new HashMap<>();
        geometrical_mean_per_student = new HashMap<>();
        showIntermediateResults = false;

        group_median = 1;
        done = false;
        readyToTest = false;
        nextStepBTN.setText("next step");
        currentDescription.setText("Add professor evlauation");
        currentDescription.setVisible(true);
        singleInputPane.setVisible(true);
        studentsInputPane.setVisible(false);
        gradeProStudentPane.setVisible(false);
        intermediateResultPane.setVisible(false);
        finalResultPane.setVisible(false);
        singleInputField.requestFocus();
    }
}
