# it takes the gold standard and predicted map and check the result, return precision and recall


class Evaluator:

    def __init__(self, gold_dict, predicted_dict):
        self.gold_dict = gold_dict
        self.predicted_dict = predicted_dict

    # given the gold standard annotation dictionary and my own annotated dictionary
    def evaluate_laterality(self, gold_dict, my_info_dict):
        true_pos = 0
        true_neg = 0
        false_neg = 0
        false_pos = 0
        for patient in my_info_dict.keys():
            if 'laterality' in gold_dict[patient] and 'laterality' not in my_info_dict[patient]:
                false_neg += 1
            if 'laterality' in my_info_dict[patient]:
                predicted_laterality = my_info_dict[patient]['laterality']
                if 'laterality' in gold_dict[patient]:
                    real_lateralityal = gold_dict[patient]['laterality']
                    if predicted_laterality == real_lateralityal:
                        true_pos += 1
                    else:
                        false_pos += 1


        precision = true_pos * 1.0 / (true_pos + false_pos)
        recall = true_pos * 1.0 / (true_pos + false_neg)
        accuracy = (true_pos + true_neg) * 1.0 / (true_pos + true_neg + false_neg + false_pos)
        return precision, recall, accuracy

    # the value of tumor_size_dimension is numeric instead of discrete,
    # so we just evaluate the accuracy
    def evaluate_tumor_size_dimension(self):
        total = 0
        true_predicts = 0
        for patient, inner in self.gold_dict.items():
            if 'tumor_size_dimension' in inner:
                total += 1
        for patient, inner in self.predicted_dict.items():
            if patient in self.gold_dict:
                if 'tumor_size_dimension' in inner and 'tumor_size_dimension' in self.gold_dict[patient]:
                    if inner['tumor_size_dimension'] == self.gold_dict[patient]['tumor_size_dimension']:
                        true_predicts += 1
        return true_predicts * 1.0 / total

    def evaluate_tumor_size_unit(self):
        total = 0
        true_predicts = 0
        for patient, inner in self.gold_dict.items():
            if 'tumor_size_unit' in inner:
                total += 1
        for patient, inner in self.predicted_dict.items():
            if patient in self.gold_dict:
                if 'tumor_size_unit' in inner and 'tumor_size_unit' in self.gold_dict[patient]:
                    if inner['tumor_size_unit'] == self.gold_dict[patient]['tumor_size_unit']:
                        true_predicts += 1
        return true_predicts * 1.0 / total

    def evaluate_tumor_number(self):
        total = 0
        true_predicts = 0
        for patient, inner in self.gold_dict.items():
            if 'tumor_number' in inner:
                total += 1
        for patient, inner in self.predicted_dict.items():
            if patient in self.gold_dict:
                if 'tumor_number' in inner and 'tumor_number' in self.gold_dict[patient]:
                    if inner['tumor_number'] == self.gold_dict[patient]['tumor_number']:
                        true_predicts += 1
        return true_predicts * 1.0 / total
