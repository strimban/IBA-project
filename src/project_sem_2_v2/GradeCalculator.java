package project_sem_2_v2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private JScrollPane finalResultPane;
    private JTable finalResultTable;
    private JPanel authorPane;
    private JLabel authorLabel;


    private DefaultTableModel studentsInputModel;
    private double teacherGrade = -1.0;
    private double zooming = -1.0;
    private List<String> studentNames = new ArrayList<>();
    private Map<String, Double> studentNameWithTeacherGrade = new HashMap<String, Double>();

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
                if (teacherGrade == -1.0){
                    get_teacher_grade();
                }
                else if (zooming == -1.0) {
                    get_zooming();
                }
                else if (studentNameWithTeacherGrade.isEmpty()){
                    student_to_map();
                }
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
                /**
                 * this is test
                 * \**/
                student_input_table();
            }
        });
    }

    public static void main(String[] args) {
        JFrame counter = new JFrame("Grade Calculator");
        counter.setContentPane(new GradeCalculator().mainWindow);
        counter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        counter.pack();
        counter.setVisible(true);
    }

    private void get_teacher_grade(){
        try {
            int tmp = Integer.parseInt(singleInputField.getText());
            if (tmp > 0 && tmp <= 100) {
                teacherGrade = tmp/100.0;
                currentDescription.setText("enter zooming parameter");
                singleInputField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Enter a grade in points between 1 and 100");
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Enter a valid grade");
        }
    }

    private void get_zooming(){
        try {
            double tmp = Double.parseDouble(singleInputField.getText());
            if (tmp > 0) {
                zooming = tmp;
                currentDescription.setText("enter zooming parameter");
                singleInputField.setEnabled(false);
                singleInputField.setText("");
                student_input_table();
                studentTablePane.setVisible(true);
                singleInputField.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "Enter a zooming parameter >= 0");
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Enter a valid number");
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
                }
                else {
                    JOptionPane.showMessageDialog(null, "enter a unique name");
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "enter a name");
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "enter a valid number");
        }

    }
    private void student_to_map() {
        DefaultTableModel dtm = (DefaultTableModel) studentTable.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Object[][] tableData = new Object[nRow][nCol];
        double tmp = 0;
        for (int i = 0; i < nRow; i++) {
            tmp += Double.parseDouble(dtm.getValueAt(i, 1).toString());
        }
        if (tmp != 100.0){
            JOptionPane.showMessageDialog(null, "the sum of teacher grades should be equal to 100");
            tmp = 0;
        }
        else {
            studentNames = new ArrayList<>();
            for (int i = 0; i < nRow; i++) {
                double teacher_grade = Double.parseDouble(dtm.getValueAt(i, 1).toString()) / 100;
                String name = dtm.getValueAt(i, 0).toString();
                studentNames.add(name);
                studentNameWithTeacherGrade.put(name, teacher_grade);
            }
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

    }
}
