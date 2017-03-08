from collections import defaultdict
import re
# this class is to extract tumor size dimension and tumor size unit


class TumorSizeAnnotation:
    def __init__(self, reports_filenames):
        self.reports_filenames = reports_filenames
        self.my_info_dict = defaultdict(lambda: defaultdict())

    def get_tumor_info(self):
        for reports_filename in self.reports_filenames:
            with open(reports_filename) as f_report:
                data = f_report.readlines()
            for line in data:
                line = line.lower()
            patient = ''
            for i in range(0, len(data)):
                line = data[i]
                line = line.lower()
                if line.startswith('<patient_display_id>'):
                    patient = data[i + 1].strip()
                    self.my_info_dict[patient] = defaultdict()
                if line.startswith('<tumor_record_number>'):
                    tumor_number = data[i + 1].strip()
                    self.my_info_dict[patient]['tumor_number'] = tumor_number
                if 'tumor size' in line or 'size' in line:
                    tumor_size_unit = ''
                    if 'cm' in line or 'cm' in data[i + 1]:
                        tumor_size_unit = 'cm'
                    if 'mm' in line or 'mm' in data[i + 1]:
                        tumor_size_unit = 'mm'
                    self.my_info_dict[patient]['tumor_size_unit'] = tumor_size_unit
                    matchObj = re.match(r'([^0-9]*)(\d+(\.\d+)?(\s)?x(\s)?\d+(\.\d+)?(\s)?x(\s)?\d+(\.\d+)?)(.*)', line)
                    if not matchObj:
                        matchObj = re.match(r'([^0-9]*)(\d+(\.\d+)?(\s)?x(\s)?\d+(\.\d+)?(\s)?x(\s)?\d+(\.\d+)?)(.*)', data[i + 1])
                    if matchObj:
                        temp = matchObj.group(2)
                        # temp = temp.split('x')
                        tumor_size_dimension = re.sub(r'x', '/', temp)
                        # tumor_size_dimension = temp[0].strip() + '／' + temp[1].strip() + '／' + temp[2].strip()
                        self.my_info_dict[patient]['tumor_size_dimension'] = tumor_size_dimension
                        continue
                if 'tumor size' not in line and 'size' in line:
                    tumor_size_unit = ''
                    if 'cm' in line or 'cm' in data[i + 1]:
                        tumor_size_unit = 'cm'
                    if 'mm' in line or 'mm' in data[i + 1]:
                        tumor_size_unit = 'mm'
                    self.my_info_dict[patient]['tumor_size_unit'] = tumor_size_unit
        return self.my_info_dict