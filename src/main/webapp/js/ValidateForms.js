/**
  * Validate all forms on the page before submitting.
  * 
  * Hook into the submit event handler of every form on a page and
  * validate its fields according to the rules denoted by their CSS
  * classes. If a form contains invalid fields, the submission will
  * be prevented and the offending field will be highlighted.
  * 
  * Validation of a form can be turned on by adding the CSS class
  * "validation-enabled".
  * 
  * Validation of individual fields is currently restricted to checking
  * that a text input field is not empty. This can be turned on by
  * marking the respective field with the CSS class "validation-not-empty".
  * 
  * Fields that are found to be invalid will be tagged with the CSS
  * class "validation-failed" and can be styled accordingly.
  */

function getInvalidEmptyTextFields(form) {
    var textfields = $(form).find("input[type='text'].validation-not-empty");
    var invalidFields = [];
    
    textfields.each(function (idx, textfield) {
        var isEmpty = textfield.value.trim() === "";
        if (isEmpty) {
            invalidFields.push(textfield);
        }
    });
    
    return invalidFields;
}

function highlightInvalidFields(fields) {
    fields.forEach(function (field) {
        $(field).addClass("validation-failed");
    });
}

function validateNonEmptyTextFields(form) {
    var invalidFields = getInvalidEmptyTextFields(form);
    
    highlightInvalidFields(invalidFields);
    
    return invalidFields.length === 0;
}


$(document).ready(function () {
    $("form.validation-enabled").each(function (idx, form) {
        var errorMessage =
            "This form contains invalid fields. Most likely you forgot " +
            "to fill in a required field.\n" +
            "Please have a look at the highlighted fields and submit again.";
        
        $(form).submit(function (event) {
            var valid = validateNonEmptyTextFields(form);
            if (!valid) {
                event.preventDefault();
                alert(errorMessage);
            }
        });
    });
});
