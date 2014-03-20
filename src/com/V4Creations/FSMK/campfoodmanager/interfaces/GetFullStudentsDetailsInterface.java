package com.V4Creations.FSMK.campfoodmanager.interfaces;

import java.util.ArrayList;

import com.V4Creations.FSMK.campfoodmanager.db.GetAllStudensDetailsHelperAsynzTask.StudentsFullDetails;

public interface GetFullStudentsDetailsInterface {
	void notifyFullDetails(ArrayList<StudentsFullDetails> studentsFullDetails);
}
