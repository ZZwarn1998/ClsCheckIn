
class Stu(object):
    def __init__(self, s_name, s_id, s_gender,s_face):
        self.name = s_name
        self.id = s_id
        self.gender = s_gender
        s_face_str = [ str(s_face[i]) for i in range(len(s_face))]
        self.face = " ".join(s_face_str)
        print(len(self.face))

    def tostring(self):
        items = ["Name", "ID", "Gender", "Face"]
        vals = [self.name, self.id, self.gender, self.face]
        item2val = dict(zip(items, vals))
        pstr = "Name:%(Name)s, ID:%(ID)s, Gender:%(Gender)s, Face:%(Face)s" % item2val
        return pstr

    def to_item(self):
        return self.id, self.name, self.gender, self.face


