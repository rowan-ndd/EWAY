from evaluation import Evaluator
from get_tumor_info import TumorSizeAnnotation
from gold_info import PatientGoldInfo

# a list of gold standard files for testing
test_file_list = ['LING_575/Annotations/annotations_1_breast_test.json', 'LING_575/Annotations/annotations_1_lung_test.json',
                  'LING_575/Annotations/annotations_2_breast_test.json', 'LING_575/Annotations/annotations_2_lung_test.json',
                  'LING_575/Annotations/annotations_4_breast_test.json', 'LING_575/Annotations/annotations_4_lung_test.json',
                  'LING_575/Annotations/annotations_5_breast_test.json', 'LING_575/Annotations/annotations_5_lung_test.json',
                  ]

# get the gold standard information
patient_gold_info = PatientGoldInfo(test_file_list)
gold_info_dict = patient_gold_info.initialize_gold_standard()

# a list of reports to test
test_report_list = ['LING_575/Reports/reports_1_breast_test.txt', 'LING_575/Reports/reports_1_lung_test.txt',
                    'LING_575/Reports/reports_2_breast_test.txt', 'LING_575/Reports/reports_2_lung_test.txt',
                    'LING_575/Reports/reports_4_breast_test.txt', 'LING_575/Reports/reports_4_lung_test.txt',
                    'LING_575/Reports/reports_5_breast_test.txt', 'LING_575/Reports/reports_5_lung_test.txt',
                    ]
# get the predict information
my_info = TumorSizeAnnotation(test_report_list)
my_info_dict = my_info.get_tumor_info()

# evaluation the result
my_evaluator = Evaluator(gold_info_dict, my_info_dict)
tumor_size_dimension_accuracy = my_evaluator.evaluate_tumor_size_dimension()
tumor_size_unit_accuracy = my_evaluator.evaluate_tumor_size_unit()
tumor_number_accuracy = my_evaluator.evaluate_tumor_number()

print('tumor_size_dimension_accuracy: ' + str(tumor_size_dimension_accuracy))
print('tumor_size_unit_accuracy: ' + str(tumor_size_unit_accuracy))
print('tumor_number_accuracy: ' + str(tumor_number_accuracy))