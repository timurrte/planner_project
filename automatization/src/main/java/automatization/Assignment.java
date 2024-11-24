package automatization;

import automatization.enums.DayOfWeek;
import automatization.enums.FormOfStudying;

public class Assignment {
	private String subject;
	private String teacher;
	private DayOfWeek day;
	private String group_of_students;
	private String time_of_lesson;
	private String classroom;
	
	private FormOfStudying form_of_studying;
	private Numerator week_type_of_lesson;
	
	public Assignment() {
		
	}
	
	public Assignment(DayOfWeek day, String time, String lesson, String teacher, 
			String classroom, String group, Numerator weekTypeIndicator) {
		this.subject = lesson;
		this.teacher = teacher;
		this.day = day;	
		this.group_of_students = group;
		this.time_of_lesson = time;
		if (classroom.equals("online")) {
			this.classroom = "";
			this.form_of_studying = FormOfStudying.ONLINE;
		} else {
			this.classroom = classroom;
			this.form_of_studying = FormOfStudying.OFFLINE;
		}
		this.week_type_of_lesson = weekTypeIndicator;
	}
	
	public String toString() {
		return "\nГрупа: " + this.getGroup() + "\nЗаняття: " + this.getSubject()
				+ "\nДень: " + this.getDay() + "\nЧас: " + this.getTime_of_lesson()
				+ "\nВикладач: " + this.getTeacher() + "\nКабінет: " + this.getClassroom() + "\nТип занять: " + this.getForm_of_studying()
				+ "\nТиждень: " + this.getWeek_type_of_lesson();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public DayOfWeek getDay() {
		return day;
	}

	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	public String getGroup() {
		return group_of_students;
	}

	public void setGroup(String group_of_students) {
		this.group_of_students = group_of_students;
	}
	
	public String getTime_of_lesson() {
		return time_of_lesson;
	}

	public void setTime_of_lesson(String time_of_lesson) {
		this.time_of_lesson = time_of_lesson;
	}
	
	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public Numerator getWeek_type_of_lesson() {
		return week_type_of_lesson;
	}

	public void setWeek_type_of_lesson(Numerator week_type_of_lesson) {
		this.week_type_of_lesson = week_type_of_lesson;
	}
	public FormOfStudying getForm_of_studying() {
		return form_of_studying;
	}

	public void setForm_of_studying(FormOfStudying form_of_studying) {
		this.form_of_studying = form_of_studying;
	}
}
