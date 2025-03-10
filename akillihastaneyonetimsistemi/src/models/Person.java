package models;
public abstract class Person{
        protected int id;
        protected String name;
        protected int age;
        protected String address; // Düzeltildi
        protected String tc;

        public Person(int id, String name, int age, String address, String tc) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.address = address;
            this.tc = tc;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getAge() { return age; }
        public void setAge(int age) {
            if (age > 0) {
                this.age = age;
            } else {
                throw new IllegalArgumentException("Age must be greater than 0.");
            }
        }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getTc() { return tc; }
        public void setTc(String tc) { this.tc = tc; }

        public void displayInfo() {
            System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age + ", Address: " + address + ", TC: " + tc);
        }

        public abstract String getRole();
    }

