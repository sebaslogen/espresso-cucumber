#!/usr/bin/python

import os
import sys
import argparse
from argparse import RawTextHelpFormatter
import subprocess
import fileinput

CWD = os.path.abspath(os.path.dirname(os.path.realpath(__file__)))
ESPRESSO_TEMAPLTE_ZIP = 'EspressoProjectTestTemplate.zip'
PROJECT_NAME_TEMPLATE_TEXT = 'PROJECT_NAME_TEMPLATE'
PKG_NAME_TEMPLATE_TEXT = 'PACKAGE_NAME_TEMPLATE'
DEFAULT_ACTIVITY_TEXT = 'DEFAULT_ACTIVITY'

class Espresso:

    @staticmethod
    def show_espresso_first_usage_help(test_project_path, project_name, steps_definition_path):
        print('\n\n'\
            'Espresso & Cucumber basic documentation\n'\
            '---------------------------------------\n'\
            '\n'\
            '> Cucumber\n'\
            'Start writing test scenarios and feature tests using Given-When-Tehn language in feature file:\n'\
            '{0}\n'\
            '\n'\
            '> Espresso\n'\
            'Implement Test Steps Definitions in file:\n'\
            '{1}\n'\
            '\n'\
            'For Espresso library examples and documentation visit: https://code.google.com/p/android-test-kit/wiki/EspressoSamples\n'.format(
                os.path.join(test_project_path, 'src', 'assets', 'features', project_name + '.feature'),
                os.path.join(test_project_path, steps_definition_path, project_name + 'TestSteps.java')))

    @staticmethod
    def replace_in_file(original_text, new_text, file_to_replace):
        if not os.path.exists(file_to_replace):
            sys.exit("Error: Destination file to replace in new test project does not exist: {0}".format(file_to_replace))
        for line in fileinput.input(file_to_replace, inplace=True):
            print(line.replace(original_text, new_text)),

    @staticmethod
    def move_source_code_to_package_path(package, path):
        original_path = os.path.join(path, 'src', 'java', 'test')
        relative_dest_path = os.path.join('src', 'java', *package.split('.'))
        destination_path = os.path.join(path, relative_dest_path, 'test')
        os.makedirs(destination_path)
        os.rename(original_path, destination_path)
        return os.path.join(relative_dest_path, 'test')

    @staticmethod
    def check_test_project_dependencies(test_project_path):
        # Check dependencies in project.properties
        project_properties_file = os.path.join(test_project_path, 'project.properties')
        properties = Espresso.read_properties(project_properties_file)
        for key in properties:
            if ('dir' in key or 'library.reference' in key):
                dep_path = os.path.join(test_project_path, properties[key])
                if not os.path.exists(dep_path):
                    print "\nWarning!\n Dependency path for compilation does not exist: {0}\
                        \n Fix dependencies path in file {1}".format(dep_path, project_properties_file)

    @staticmethod
    def read_properties(file_to_read):
        properties = {}
        for line in open(file_to_read):
            #H = dict(line.strip().split('=')
            if (len(line.strip()) == 0) or (line.strip()[0] == '#'): continue
            properties[line.strip().split('=')[0]] = line.strip().split('=')[1]
        return properties

    @staticmethod
    def generate_test(package, project_name, destination_path, activity_name):
        if not os.path.isdir(destination_path):
            sys.exit("Error: Destination path to place the new test project does not exist" +
                ", first create it: {0}".format(destination_path))
        espresso_template_zip = os.path.abspath(os.path.join(CWD, ESPRESSO_TEMAPLTE_ZIP))
        if not os.path.exists(espresso_template_zip):
            sys.exit("Error: Espresso template zip file does not exist, please check: {0}".format(espresso_template_zip))
        template_path = os.path.join(destination_path, 'PROJECT_NAME_TEMPLATETest')
        test_project_path = os.path.join(destination_path, project_name + 'EspressoTest')
        if os.path.isdir(template_path):
            sys.exit("Error: Temporal destination path already exists, delete or rename it first: {0}".format(template_path))
        if os.path.isdir(test_project_path):
            sys.exit("Error: Destination path already exists, delete or rename it first: {0}".format(test_project_path))
        
        print "Extracting template test project..."
        cmd = ['unzip', espresso_template_zip, '-d', destination_path]
        p = subprocess.Popen(cmd)
        output = p.communicate()
        if p.returncode != 0:
            sys.exit("Error unzipping Espresso template zip: \n"
                      + '$ '+ ' '.join(cmd) + "\n"
                      + "Exiting.")
        if not os.path.isdir(destination_path):
            sys.exit("Error unzipping Espresso template zip ({0}) to {1}".format(espresso_template_zip, template_path))

        print "\nConfiguring test template project..."

        # Fill classes and variable names with target package and project names
        Espresso.replace_in_file(PROJECT_NAME_TEMPLATE_TEXT, project_name, os.path.join(template_path, 'build.xml'))
        Espresso.replace_in_file(PROJECT_NAME_TEMPLATE_TEXT, project_name, os.path.join(template_path, '.project'))
        Espresso.replace_in_file(PKG_NAME_TEMPLATE_TEXT, package, os.path.join(template_path, 'AndroidManifest.xml'))
        Espresso.replace_in_file(PROJECT_NAME_TEMPLATE_TEXT, project_name, os.path.join(template_path, 'project.properties'))
        Espresso.replace_in_file(PROJECT_NAME_TEMPLATE_TEXT, project_name, os.path.join(template_path, '.classpath'))
        Espresso.replace_in_file(PKG_NAME_TEMPLATE_TEXT, package, os.path.join(template_path, 
            'src', 'java', 'test', 'RunCucumberTest.java'))
        Espresso.replace_in_file(PKG_NAME_TEMPLATE_TEXT, package, os.path.join(template_path, 
            'src', 'java', 'test', 'PROJECT_NAME_TEMPLATETestSteps.java'))
        Espresso.replace_in_file(PROJECT_NAME_TEMPLATE_TEXT, project_name, os.path.join(template_path, 
            'src', 'java', 'test', 'PROJECT_NAME_TEMPLATETestSteps.java'))
        if activity_name: Espresso.replace_in_file(DEFAULT_ACTIVITY_TEXT, 
            activity_name, os.path.join(template_path, 'src', 'java', 'test', 'PROJECT_NAME_TEMPLATETestSteps.java'))
        Espresso.replace_in_file(PROJECT_NAME_TEMPLATE_TEXT, project_name, os.path.join(template_path,
            'src', 'assets', 'features', PROJECT_NAME_TEMPLATE_TEXT + '.feature'))

        # Rename files and folders to align with target package and project names
        ("path/to/current/file.foo", "path/to/new/desination/for/file.foo")
        os.rename(os.path.join(template_path, 'src', 'java', 'test', 'PROJECT_NAME_TEMPLATETestSteps.java'), 
            os.path.join(template_path, 'src', 'java', 'test', project_name + 'TestSteps.java'))
        os.rename(os.path.join(template_path, 'src', 'assets', 'features', 'PROJECT_NAME_TEMPLATE.feature'), 
            os.path.join(template_path, 'src', 'assets', 'features', project_name + '.feature'))
        # Generate package tree from 'package' under src/java/ and move test package folder
        steps_definition_path = Espresso.move_source_code_to_package_path(package, template_path)
        os.rename(template_path, test_project_path)

        # Check relative paths to app and libraries
        Espresso.check_test_project_dependencies(test_project_path)
        print "Test project template configured"
        Espresso.show_espresso_first_usage_help(test_project_path, project_name, steps_definition_path)


if __name__ == "__main__":

    parser = argparse.ArgumentParser(description='Create test project with Cucumber and Espresso from template\
        \nFor Espresso library examples and documentation visit: https://code.google.com/p/android-test-kit/wiki/EspressoSamples',
        formatter_class=RawTextHelpFormatter)
    parser.add_argument('action', help='The action to perform (e.g. \'generate\' or just \'g\')')
    parser.add_argument('element', help='The element to perform the action (e.g. \'test\' or just \'t\')')
    parser.add_argument('package', help='The package of the target application to test (e.g. com.tomtom.pnd.firstrunwizard)')
    parser.add_argument('-d', '--destination-path', dest='destination_path', default=os.path.abspath(os.path.dirname('.')),
        help='Path inside which the test project will be created and placed (e.g. .../MyAppProject/test)')
    parser.add_argument('-p', '--project-name', dest='project_name',
        help='Name of the project to test (last part of package name is used by default)')
    parser.add_argument('-a', '--default-activity', dest='activity',
        help='Name of the main activity to test (test will instrument this activity as starting point, e.g. HomeActivity)')

    # Parse the arguments
    options = parser.parse_args()
    if not ((options.action == 'generate') or (options.action == 'g')):
        sys.exit("Error: Unrecognized or missing action (e.g. generate)")
    if not ((options.element == 'test') or (options.element == 't')):
        sys.exit("Error: Unrecognized or missing element (e.g. test)")
    if not (options.package):
        sys.exit("Error: Missing input package to test")
    if not options.project_name:
        options.project_name = options.package.split('.')[-1]
        if (len(options.project_name) > 1):
            options.project_name = options.project_name[0].upper() + options.project_name[1:]
    options.destination_path = os.path.abspath(options.destination_path)

    Espresso.generate_test(options.package, options.project_name, options.destination_path, options.activity)