package project_sem_2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GradeCalculator {
    private JPanel main_window;
    private JLabel program_title;
    private JLabel current_description;
    private JPanel single_action_pane;
    private JFormattedTextField single_input;
    private JButton single_submit_btn;
    private JPanel multiple_action_pane;
    private JLabel first_field_label;
    private JLabel second_field_label;
    private JFormattedTextField first_field_text;
    private JFormattedTextField second_field_text;
    private JButton multiple_submit_btn;
    private JLabel placeholder1;
    private JLabel result_grade_label;
    private JPanel result_panel;
    private JButton next_student_btn;
    private JLabel author_label;
    private JTable result_table;
    private JScrollPane scroll_pane;

    private int number_of_students = 0;
    private boolean first_run = true;
    private double teacher_grade = 0.0;
    private Map<String, Map<String, Double>> students_results = new HashMap<>();
    private Map<String, Double> geometrical_mean_per_student = new HashMap<String, Double>();
    private String evaluete_person_name = "";
    private Map<String, Double> tmp_student_map = new HashMap<>();
    private int tmp_person_counter = 1;
    private List<String> students_list_new = new ArrayList<>();
    private List<String> students_list_done = new ArrayList<>();
    private List<String> students_list_activ = new ArrayList<>();
    private double geometrical_sum;
    private boolean is_done = false;


    private void table(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Student Name");
        model.addColumn("grade in points");
        model.addColumn("grade in german system");
        for (Map.Entry<String, Double> entry : geometrical_mean_per_student.entrySet())
        {
            double person_median = entry.getValue();
            double grade_in_hundert = (person_median*teacher_grade)/((person_median*teacher_grade) + geometrical_sum*(1-teacher_grade));
            grade_in_hundert *= 100;
            grade_in_hundert = Math.round(grade_in_hundert);
            double iba_grade;
            if (grade_in_hundert < 50) iba_grade = 5.0;
            else if (grade_in_hundert <= 55) iba_grade = 4.0;
            else if (grade_in_hundert <= 60) iba_grade = 3.7;
            else if (grade_in_hundert <= 65) iba_grade = 3.3;
            else if (grade_in_hundert <= 70) iba_grade = 3.0;
            else if (grade_in_hundert <= 75) iba_grade = 2.7;
            else if (grade_in_hundert <= 80) iba_grade = 2.3;
            else if (grade_in_hundert <= 85) iba_grade = 2.0;
            else if (grade_in_hundert <= 90) iba_grade = 1.7;
            else if (grade_in_hundert <= 95) iba_grade = 1.3;
            else iba_grade = 1.0;
            model.addRow(new Object[]{entry.getKey(), (int)grade_in_hundert, iba_grade});
        }

        result_table.setModel(model);
        scroll_pane.setViewportView(result_table);
    }

    private void person_info_getter(){
        String person_who_evaluate = "";
        tmp_person_counter += 1;
        current_description.setText("classmate number " + tmp_person_counter + " evaluate " + evaluete_person_name);
        try {
            double grade_tmp = Integer.parseInt(second_field_text.getText());

            if (grade_tmp >= 0) {
                String name_tmp = first_field_text.getText();
                if (!name_tmp.equals("")) {
                    if (!tmp_student_map.containsKey(name_tmp) && !name_tmp.equals(evaluete_person_name)) {
                        tmp_student_map.put(name_tmp, grade_tmp);
                        first_field_text.setText(person_who_evaluate);
                        second_field_text.setText("");
                        if (first_run) {
                            students_list_new.add(name_tmp);
                            first_field_text.requestFocusInWindow();
                        }
                        else {
                            second_field_text.requestFocusInWindow();
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "This name is taken, enter another name");
                        tmp_person_counter -= 1;
                        current_description.setText("Ask classmate number " + tmp_person_counter + " to evaluate you");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Enter your Name");
                    tmp_person_counter -= 1;
                    current_description.setText("Ask classmate number " + tmp_person_counter + " to evaluate you");
                }
                if (!students_list_activ.isEmpty()) try {
                    person_who_evaluate = students_list_activ.get(tmp_person_counter - 1);
                    first_field_text.setText(person_who_evaluate);
                } catch (Exception ignored) {}
            } else {
                JOptionPane.showMessageDialog(null, "Enter a grade >= 0");
                tmp_person_counter -= 1;
                current_description.setText("Ask classmate number " + tmp_person_counter + " to evaluate you");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "enter a valid number");
            tmp_person_counter -= 1;
            current_description.setText("Ask classmate number " + tmp_person_counter + " to evaluate you");
        }

        if (tmp_person_counter == number_of_students) {
            first_run = false;
            // add evaluetion of activ student to the map
            students_results.put(evaluete_person_name, tmp_student_map);
            // add acriv student to done list
            students_list_done.add(evaluete_person_name);
            // reset tmp student map
            get_geometrical_mean(tmp_student_map);
            geometrical_mean_per_student.put(evaluete_person_name, geometrical_sum);
            tmp_student_map = new HashMap<>();
            // reset person counter
            tmp_person_counter = 1;
            // reset list of students who evaluate

            students_list_activ = new ArrayList<>();
            if (!students_list_new.isEmpty()){
                result_grade_label.setText(evaluete_person_name + "'s result is " + geometrical_sum);
                evaluete_person_name = students_list_new.get(0);
                students_list_new.remove(0);
                students_list_activ.addAll(students_list_new);
                students_list_activ.addAll(students_list_done);
                first_field_text.setText(students_list_activ.get(0));
                current_description.setText("classmate number " + tmp_person_counter + " evaluate " + evaluete_person_name);
                person_who_evaluate = students_list_activ.get(0);
                first_field_text.setText(person_who_evaluate);
                first_field_text.setEnabled(false);
                multiple_action_pane.setVisible(false);
                result_panel.setVisible(true);
                second_field_label.requestFocusInWindow();
            }
            else {
                result_grade_label.setText(evaluete_person_name + "'s result is " + geometrical_sum);
                get_geometrical_mean(geometrical_mean_per_student);
                next_student_btn.setText("show results");
                is_done = true;
                multiple_action_pane.setVisible(false);
                result_panel.setVisible(true);
            }

        }

    }

    private GradeCalculator() {
        single_submit_btn.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (number_of_students == 0) {
                    set_students_number();
                }
                else if (teacher_grade == 0) {
                    get_teacher_grade();
                }
                else {
                    get_first_person_name();
                }
            }
        });
        single_input.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (number_of_students == 0) {
                    set_students_number();
                }
                else if (teacher_grade == 0) {
                    get_teacher_grade();
                }
                else {
                    get_first_person_name();
                }
            }
        });

        multiple_submit_btn.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                person_info_getter();
            }
        });

        second_field_text.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                person_info_getter();
            }
        });
        next_student_btn.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!is_done) {
                    multiple_action_pane.setVisible(true);
                    result_panel.setVisible(false);
                    first_field_text.requestFocusInWindow();
                }
                else {
                    current_description.setText(" ");
                    result_panel.setVisible(false);
                    scroll_pane.setVisible(true);
                    table();
                }
            }
        });
        first_field_text.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                second_field_text.requestFocusInWindow();
            }
        });
    }

    private void get_teacher_grade(){
        try {
            int tmp = Integer.parseInt(single_input.getText());
            if (tmp > 0 && tmp <= 100) {
                teacher_grade = tmp/100.0;
                single_input.setText("");
                current_description.setText("Enter name of first person to evaluate");
            } else {
                JOptionPane.showMessageDialog(null, "Enter a grade in points between 1 and 100");
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Enter a valid grade");
        }
    }

    private void get_first_person_name(){
        single_action_pane.setVisible(false);
        multiple_action_pane.setVisible(true);
        String tmp = single_input.getText();
        if (!tmp.equals("")) {
            evaluete_person_name = single_input.getText();
            current_description.setText("classmate number " + tmp_person_counter + " evaluate " + evaluete_person_name);
            first_field_text.requestFocusInWindow();
        }
        else {
            JOptionPane.showMessageDialog(null, "Enter your Name");
        }
    }

    private void set_students_number(){
        try {
            int tmp = Integer.parseInt(single_input.getText());
            if (tmp > 0) {
                number_of_students = tmp;
                current_description.setText("Ask teacher to enter his grade");
                single_input.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Enter a positive number ");
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Enter a valid number");
        }
    }

    private static double round(double value) {

        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void get_geometrical_mean(Map<String, Double> to_analyze){
        List<Double> list_of_grades = new ArrayList<>(to_analyze.values());
        double sum = 1;
        for (Double list_of_grade : list_of_grades) {
            sum = list_of_grade * sum;
        }
        geometrical_sum = round(Math.pow(sum, 1.0 / list_of_grades.size()));
    }

    public static void main(String[] args) {
        JFrame counter = new JFrame("Grade Calculator");
        counter.setContentPane(new GradeCalculator().main_window);
        counter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        counter.pack();
        counter.setVisible(true);
    }
}
