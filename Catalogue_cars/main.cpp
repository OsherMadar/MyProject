#include <iostream>
#include <cstring>

using namespace std;

class Vehicle{
protected:
    char *id;
public:
    Vehicle(char* id){
        this->id = new char [strlen(id)+1];
        strcpy(this->id , id);
    }

    virtual ~Vehicle(){
        delete [] id;
    }

    Vehicle& operator=(const Vehicle &v){
        delete [] id;
        id = new char [strlen(v.id)+1];
        strcpy(id , v.id);
        return *this;
    }

    Vehicle(const Vehicle &v){
        id = NULL;
        operator=(v);
    }

    virtual void print()const=0;

    virtual Vehicle* colne()const=0;
};


class Car : virtual public Vehicle{
    friend class Boat;
protected:
    int land_speed;
public:
    Car(char* id , int l_s): Vehicle(id){
        land_speed = l_s;
    }

    virtual ~Car(){}

    /*Car& operator=(const Car &c){
        delete [] id;
        id = new char [strlen(c.id)+1];
        strcpy(id , c.id);
        land_speed = c.land_speed;
        return *this;
    }

    Car(const Car &c): Vehicle(c){
        land_speed = 0;
        land_speed = c.land_speed;
    }*/

    Vehicle* colne()const{
        return new Car(*this);
    }

    void print()const{
        cout << "car: " << id << "\n" << "speed- " << land_speed << endl;
    }
};


class Boat : virtual public Vehicle{
protected:
    int marine_speed;
public:
    Boat(char *id , int m_s): Vehicle(id){
        marine_speed = m_s;
    }

    virtual ~Boat(){}

    /*Boat& operator=(const Boat &b){
        delete [] id;
        id = new char [strlen(b.id)+1];
        strcpy(id , b.id);
        marine_speed = b.marine_speed;
        return *this;
    }

    Boat(const Boat &b): Vehicle(b){
        marine_speed = 0;
        marine_speed = b.marine_speed;
    }*/

    Vehicle* colne()const{
        return new Boat(*this);
    }

    Boat(const Car &c): Vehicle(c){
        marine_speed = c.land_speed;
    }

    void print()const{
        cout << "Boat: " << id << "\n" << "speed- " << marine_speed << endl;
    }
};


class Amphibian : public Car , public Boat {
public:
    Amphibian(char *id, int l_s, int m_s) : Vehicle(id), Car(id, l_s), Boat(id, m_s) {}

    virtual ~Amphibian(){}

    Vehicle* colne()const{
        return new Amphibian(*this);
    }

    void print() const {
        cout << "Amphibian: " << id << "\n" << "land speed- " << land_speed << endl
             << "marine speed- " << marine_speed << endl;
    }
};

    class Catalogue{
        int size;
        Vehicle *vehicles[1000];
    public:
        Catalogue(){
            size = 0;
        }

        ~Catalogue(){
            for(int i=0 ; i<size ; i++){
                delete vehicles[i];
            }
        }

        Catalogue& operator=(const Catalogue &c){
            for(int i=0 ; i<size ; i++){
                delete vehicles[i];
            }
            size = c.size;
            for(int j=0 ; j<size ; j++){
                vehicles[j] = c.vehicles[j]->colne();
            }
            return *this;
        }

        Catalogue(const Catalogue &c){
            size = 0;
            operator=(c);
        }

        void add(const Vehicle *v){
            vehicles[size] = v->colne();
            size++;
        }

        void print() const{
            cout << "Catalogue: \n" << "Number of vehicles: " << size << endl;
            for(int i=0 ; i<size ; i++) {
                cout << i + 1 << ". ";
                vehicles[i]->print();
                cout << endl;
            }
        }
};

    ostream& operator<<(ostream &cout , const Catalogue &c){
        c.print();
        return cout;
    }

int main() {
    Catalogue cat;
    Vehicle * p = new Car("12-333-45"  ,180);
    cat.add(p);
    p = new Boat("13-333-45" , 33);
    cat.add(p);
    p = new Amphibian("13-333-45" , 12 , 130);
    cat.add(p);

    cout << cat << endl;

    Car c1("777-888" , 190);
    Boat b1 = c1;
    b1.print();

    return 0;
}
