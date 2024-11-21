package automatization;

public class Assignment {
	private String subject;
	private String teacher;
	private String subject_abbr;
	private String day;
	private String group_of_students;
	private String form_of_lesson;
	private String form_of_studying;
	private String time_of_lesson;
	private Numerator week_type_of_lesson;
	
	public Assignment() {
		
	}
	
	public Assignment(String day, String time, String lesson, String teacher, String group, Numerator partIndicator) {
		this.day = day;
		this.time_of_lesson = time;
		this.week_type_of_lesson = partIndicator;
		this.subject = lesson;
		this.setTeacher(teacher);
		System.out.println("Day: "+ day);
		System.out.println("Time: "+ time);
		System.out.println("Assignment: " + lesson);
		System.out.println("Teacher: " + teacher);
		System.out.println("Group: " + group);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject_abbr() {
		return subject_abbr;
	}

	public void setSubject_abbr(String subject_abbr) {
		this.subject_abbr = subject_abbr;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getGroup_of_students() {
		return group_of_students;
	}

	public void setGroup_of_students(String group_of_students) {
		this.group_of_students = group_of_students;
	}

	public String getForm_of_lesson() {
		return form_of_lesson;
	}

	public void setForm_of_lesson(String form_of_lesson) {
		this.form_of_lesson = form_of_lesson;
	}

	public String getForm_of_studying() {
		return form_of_studying;
	}

	public void setForm_of_studying(String form_of_studying) {
		this.form_of_studying = form_of_studying;
	}

	public String getTime_of_lesson() {
		return time_of_lesson;
	}

	public void setTime_of_lesson(String time_of_lesson) {
		this.time_of_lesson = time_of_lesson;
	}

	public Numerator getWeek_type_of_lesson() {
		return week_type_of_lesson;
	}

	public void setWeek_type_of_lesson(Numerator week_type_of_lesson) {
		this.week_type_of_lesson = week_type_of_lesson;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
}
