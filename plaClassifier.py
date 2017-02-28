import argparse
import json
import sys
import random
import numpy as np

import torch
from torch.autograd import Variable as V
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim

def getFeatures(report_type,records):
    breast_records, behave_records = records
    id_feats = subprocess.Popen(["cat dataset/reports_5_"+report_type+"* \
        | jq -r \'. as $in | keys[] | .+\"\t\"+$in[.].TEXT_PATH_CLINICAL_HISTORY \
        +\"\t\"+$in[.].TEXT_PATH_FORMAL_DX+\"\t\"+$in[.].TEXT_PATH_GROSS_PATHOLOGY \
        +\"\t\"+$in[.].TEXT_PATH_MICROSCOPIC_DESC\'"], stdout=subprocess.PIPE,shell=True).communicate()[0].split('\n')
    for patient in id_feats:
        if patient in '': continue
        key = (patient.split('\t')[0])        
        if key in breast_records.keys():
            breast_records[key] += patient.split('\t')[1:]  
        if key in behave_records.keys():
            behave_records[key] += patient.split('\t')[1:] 
    for key, val in breast_records.iteritems():
        print(key,val)
    return breast_records, behave_records
    
def getLabel(report_type):
    id_br_be = subprocess.Popen(["jq --raw-output \'.[].PatientId + \
        \" \" + .[].Annotations[].\"Behavior Category\" + \" \" + . \
        [].Annotations[].\"Laterality Category\"\' Annotations/annotations_*_" + report_type + "*"], \
        stdout=subprocess.PIPE,shell=True).communicate()[0].split('\n')

    breast_records = defaultdict()
    behave_records = defaultdict()
    for patient in id_br_be:
        if patient in '': continue
        key = patient.split()[0]
        feats = patient.split()[1:]
        if len(feats)==2: 
            breast_records[key] = [feats[1]]
        elif len(feats)==1:
            behave_records[key] = [feats[0]]
    print("Total breast records: ", len(breast_records.keys()) )
    print("Total behavior records: ", len(behave_records.keys()) )
    return breast_records, behave_records

if __name__ == '__main__':
    import argparse
    import subprocess
    from collections import defaultdict

    ps = argparse.ArgumentParser()
    ps.add_argument('--emb', type=int, default=50)
    ps.add_argument('--hid', type=int, default=300)
    ps.add_argument('--epoch', type=int, default=10)
    ps.add_argument('--l2', type=float, default=1e-3)
    ps.add_argument('--lr', type=float, default=1e-3)
    ps.add_argument('--l', type=bool, default=False)
    args = ps.parse_args()

    if args.l:
        report_type = 'l'
    else:
        report_type = 'b'

    # Load data
    breast_records, behave_records = getFeatures(report_type,(getLabel(report_type)))
    




