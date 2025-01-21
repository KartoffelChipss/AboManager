package org.strassburger.subscriptionmanager.view;

import org.strassburger.subscriptionmanager.model.entity.BillingPeriod;
import org.strassburger.tui4j.formatting.Printer;
import org.strassburger.tui4j.formatting.TextFormatter;
import org.strassburger.tui4j.input.ContinueInput;
import org.strassburger.tui4j.input.DoubleInput;
import org.strassburger.tui4j.input.SelectInput;
import org.strassburger.tui4j.input.TextInput;
import org.strassburger.tui4j.input.validationrules.NumberValidationRules;
import org.strassburger.tui4j.input.validationrules.TextValidationRules;
import org.strassburger.tui4j.input.validationrules.ValidationRule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AddSubscriptionView {

    public void sendStartMessage() {
        Printer.println("");
        Printer.println("&a&nAdd Subscription");
        Printer.println("&7Please enter the following information:");
        Printer.println("");
    }

    /**
     * Reads the name of the subscription from the UI
     * @param nameAlreadyExistsValidationRule Validation rule to check if the name already exists.
     * @return The name of the subscription.
     */
    public String readName(ValidationRule<String> nameAlreadyExistsValidationRule) {
        return new TextInput()
                .setLabel("Name: ")
                .setRetryOnInvalid(true)
                .setInline(true)
                .addValidationRules(
                        TextValidationRules.minLength(1),
                        TextValidationRules.maxLength(255),
                        nameAlreadyExistsValidationRule
                )
                .read();
    }

    /**
     * Reads the billing period from the UI
     * @return The billing period.
     */
    public BillingPeriod readBillingPeriod() {
        return new SelectInput<BillingPeriod>()
                .addOptions(
                        Arrays.stream(BillingPeriod.values())
                                .map(bp -> new SelectInput.Option<BillingPeriod>(bp.getDisplayName(), bp))
                                .toList()
                )
                .setLabel("Billing Period")
                .setRetryOnInvalid(true)
                .read();
    }

    /**
     * Reads the price from the UI
     * @return The price.
     */
    public double readPrice() {
        return new DoubleInput()
                .setLabel("Price: ")
                .setRetryOnInvalid(true)
                .addValidationRules(
                        NumberValidationRules.greaterThan(0.0)
                )
                .read();
    }

    /**
     * Reads the start date from the UI
     * @return The start date in milliseconds since epoch.
     */
    public Long readStartDate() {
        String dateString = new TextInput()
                .setLabel("Start Date of Subscription (dd.MM.yyyy or space for no start date): ")
                .setInline(true)
                .setRetryOnInvalid(true)
                .addValidationRules(
                        //TextValidationRules.regex("\\b(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.(\\d{4})\\b", "Date must be in the format dd.MM.yyyy")
                        new ValidationRule<String>() {
                            @Override
                            public boolean validate(String s) {
                                if (s.trim().isEmpty() || s.equalsIgnoreCase("none")) return true;

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                dateFormat.setLenient(false);

                                try {
                                    Date date = dateFormat.parse(s);
                                    return true;
                                } catch (ParseException e) {
                                    return false;
                                }
                            }

                            @Override
                            public String getErrorMessage() {
                                return TextFormatter.format("&cDate must be in the format dd.MM.yyyy");
                            }
                        }
                )
                .read();

        if (dateString.isEmpty()) return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);

        try {
            Date date = dateFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            return null;
        }
    }

    public void sendSubscriptionAddSuccessMessage() {
        Printer.println("&aSubscription added successfully.");
    }

    public void enterToContinue() {
        new ContinueInput()
                .setLabel("Press ENTER to continue")
                .read();
    }

}
