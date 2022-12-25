import pymysql
from classes.Stu import Stu

class DataBase(object):
    def __init__(self, host, port, user, pwd, database):
        self.host = host
        self.port = port
        self.user = user
        self.pwd = pwd
        self.database = database

    def test_connection(self):
        try:
            conn = pymysql.connect(host=self.host, user=self.user, password=self.pwd, port=self.port)
            with conn.cursor() as cur:
                cur.execute("show databases;")
                dbs = cur.fetchall()
                if (f'{self.database}',) not in dbs:
                    cur.execute(f'create database {self.database};')
                    print(f"New database, {self.database}, has been created.")
                else:
                    print(f"Database, {self.database}, already exists.")
            conn.close()
        except Exception as e:
            print(e)
            return False
        return True

    def init(self):
        sql_create_stu_info_tb = ""
        sql_create_tchr_info_tb = ""
        sql_create_cls_info_tb = ""
        sql_create_teaching_tb = ""
        sql_create_choosing_tb = ""

    def update(self, stu: Stu):
        if self.test_connection():
            conn = pymysql.connect(host=self.host, user=self.user, password=self.pwd, port=self.port, database=self.database)
            cur = conn.cursor()
            try:
                sql = "REPLACE INTO stu_info_tb" \
                      "(stu_id, stu_name, gender, face) " \
                      "VALUES " \
                      "('%s', '%s', '%s', '%s')" % (stu.to_item())
                cur.execute(sql)
                conn.commit()
            except Exception as e:
                conn.rollback()
                print(e)
            finally:
                conn.close()
        else:
            print("Unavailable connection.")

    def get_attendance_tb(self, cls_id: str):
        stu_ids = []
        stu_names = []
        stu_faces = []
        if self.test_connection():
            conn = pymysql.connect(host=self.host, user=self.user, password=self.pwd, port=self.port,
                                   database=self.database)
            cur = conn.cursor()
            try:
                sql = "SELECT b.stu_id, b.stu_name, b.face " \
                      "FROM choosing_tb a INNER JOIN stu_info_tb b ON a.stu_id = b.stu_id " \
                      "WHERE cls_id='%s'" % cls_id
                cur.execute(sql)
                results = cur.fetchall()
                # print(results)
                for item in results:
                    # print(item)
                    stu_id = item[0]
                    stu_name = item[1]
                    face = self.face_str2face_float_ls(item[2])
                    stu_ids.append(stu_id)
                    stu_names.append(stu_name)
                    stu_faces.append(face)
            except Exception as e:
                print(e)
            finally:
                conn.close()
            return stu_ids, stu_names, stu_faces
        else:
            print("Unavailable connection.")
            return None, None, None

    def face_str2face_float_ls(self, face_str: str):
        splits = face_str.split(" ")
        face = [float(item) for item in splits]
        return face


# if __name__ == "__main__":
#     host = "localhost"
#     port = 3306
#     user = "root"
#     pwd = "zzwarn"
#     database = "cls_check_in_db"
#     db = DataBase(host, port, user, pwd, database)
#     stu = Stu()
#     db.update()
