/**
 * This package implements a custom XSLT-Function for the Saxon Transformer.
 *
 * Prefix: uibk
 * NS-URL: http://www.uibk.ac.at/igwee/ns
 * Function name: diff
 * Parameter: text1, text2
 * Returns: Error when less than two parameters are given.
 *          A string sequence when diff is performed successfully.
 *          The String sequence is organized as such: First character is either -, 0 or +; the following sequence
 *          is the string.
 *          A '-' means deletion
 *          A '0' means equality
 *          A '+' means addition.
 *
 * Eg: uibk:diff('ABCDEF', 'ABDCEFG') will result in
 * '0AB', '-C', '0D', '+C', '0EFG'
 *
 *
 * Created by joseph on 2/29/16.
 */
package at.ac.uibk.kofnego.editing.xslt.extend.diff;