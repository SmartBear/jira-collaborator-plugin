/*
 *  Copyright 2015 SmartBear Software, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at

 *     http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.smartbear.collaborator.json.collab;

public enum Action {
	/**
	 * Unknown type.
	 */
	UNKNOWN,
	/**
	 * File is marked for being added to version control.
	 */
	ADDED,
	/**
	 * File is being modified in the version control.
	 */
	MODIFIED,
	/**
	 * File is marked for being deleted from version control.
	 */
	DELETED,
	/**
	 * File has been restored to the revision in version control.
	 */
	REVERTED,
	/**
	 * File state is added directory. (ClearCase)
	 */
	ADDEDDIRECTORY,
	/**
	 * File state is include. (AccuRev only)
	 */
	INCLUDED,
	/**
	 * File state is exclude. (AccuRev only)
	 */
	EXCLUDED,
	/**
	 * Directory state is include dir. (AccuRev only)
	 */
	INCLUDED_DIR_ONLY,
	/**
	 * Directory state is include dir and descendants. (AccuRev only)
	 */
	INCLUDED_DIR,
	/**
	 * Directory state is exclude dir and descendants. (AccuRev only)
	 */
	EXCLUDED_DIR,

	// note that I am not sure about the comments on the next three:

	/**
	 * File was uploaded directly to Collaborator
	 */
	UPLOADED,
	/**
	 * File was branched
	 */
	BRANCHED,
	/**
	 * File was integrated from a different branch
	 */
	INTEGRATED;
}
