import json
from collections import defaultdict


class PatientGoldInfo:
    # it takes a list of annotation filenames
    def __init__(self, annotation_filanames):
        self.annotation_filanames = annotation_filanames
        self.patient_info_dict = defaultdict(lambda: defaultdict())

    def initialize_gold_standard(self):
        for annotation_filaname in self.annotation_filanames:
            with open(annotation_filaname) as f_anno:
                data = json.load(f_anno)
            for patient in data.keys():
                # if 'Laterality Category' in data[patient]['Annotations']['1']:
                #     patient_info_dict[data[patient]['PatientId']]['laterality'] = data[patient]['Annotations']['1']['Laterality Category']
                if 'Tumor Size-dimension' in data[patient]['Annotations']['1']:
                    self.patient_info_dict[data[patient]['PatientId']]['tumor_size_dimension'] = data[patient]['Annotations']['1']['Tumor Size-dimension']
                if 'Tumor Size-unit of measure' in data[patient]['Annotations']['1']:
                    self.patient_info_dict[data[patient]['PatientId']]['tumor_size_unit'] = data[patient]['Annotations']['1']['Tumor Size-unit of measure']
                if 'Tumor Number' in data[patient]['Annotations']['1']:
                    self.patient_info_dict[data[patient]['PatientId']]['tumor_number'] = data[patient]['Annotations']['1']['Tumor Number']
        return self.patient_info_dict