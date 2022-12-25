import json
import time

import face_recognition
import cv2
import base64
import numpy as np
import torch
from classes.Stu import Stu
from classes.DataBase import DataBase
import click

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(device)


@click.group()
def cmd():
    pass


@click.command('update_stu', help="Student registration")
@click.option('--s_name', type=str, help='The name of the student')
@click.option('--s_id', type=str, help='The id of the student')
@click.option('--s_gen', type=str, help='The Gender of the student')
@click.option('--s_face_path', type=str, help='The path of the student\'s face image')
def update_stu(s_name: str,
               s_id: str,
               s_gen: str,
               s_face_path: str):
    db = init_db()
    print(s_face_path)
    img = cv2.imread(s_face_path)
    s_img_gbr = cv2.resize(img, (0, 0), fx=0.8, fy=0.8)
    s_img_rgb = s_img_gbr[:, :, ::-1]
    face_locs = face_recognition.face_locations(s_img_rgb)
    print(type(s_img_rgb))

    if len(face_locs) != 1:
        raise Exception("One face per image, please!")
    else:
        (t, r, b, l) = face_locs[0]
        s_face = face_recognition.face_encodings(s_img_rgb, face_locs, model="large")[0]
        (h, w, _) = s_img_gbr.shape
        stu = Stu(s_name, s_id, s_gen, s_face)
        db.update(stu)


@click.command("find_absent_stu", help="Find absent students")
@click.option("--img_path", type=str, help="The path of the class attendance photo")
@click.option("--cls_id", type=str, help="The ID of class")
@click.option("--save_path", type=str, help="The path of txt file containing the result")
def find_absent_stu(img_path: str, cls_id: str, save_path: str):
    ids, names, faces = get_cls_attendance_tb(cls_id)
    id_name = list(zip(ids, names))
    absent = set()
    present = set()
    attendance = set(id_name)

    img = cv2.imread(img_path)
    s_img = cv2.resize(img, (0, 0), fx=0.8, fy=0.8)
    present_stu_face_locs = face_recognition.face_locations(s_img, number_of_times_to_upsample=2, model="hog")
    present_stu_faces = face_recognition.face_encodings(s_img, present_stu_face_locs, model="large")

    for present_stu_face in present_stu_faces:
        result = face_recognition.compare_faces(faces, present_stu_face, 0.45)
        try:
            index = result.index(True)
            # print(names[index], result)
            present.add((ids[index], names[index]))
        except Exception as e:
            print(e)
    absent = attendance.difference(present)
    result = ""
    for id, name in absent:
        result = result + name + "(" + str(id) + ")\\n"

    with open(save_path, "w+") as f:
        f.write(result)
        f.close()

def init_db():
    host = "localhost"
    port = 3306
    user = "root"
    pwd = "zzwarn"
    database = "cls_check_in_db"
    db = DataBase(host, port, user, pwd, database)
    return db


def get_cls_attendance_tb(cls_id: str):
    db = init_db()
    ids, names, faces = db.get_attendance_tb(cls_id)
    return ids, names, faces


def img2base64(img: np.ndarray):
    bytes_ = base64.b64encode(cv2.imencode(".jpg", img)[1])
    return bytes_


def base642img(bytes_: bytes):
    img = cv2.imdecode(np.fromstring(base64.b64decode(bytes_), np.uint8), cv2.IMREAD_COLOR)
    return img

cmd.add_command(update_stu)
cmd.add_command(find_absent_stu)

if __name__=="__main__":
    cmd()