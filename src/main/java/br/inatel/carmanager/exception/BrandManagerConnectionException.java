package br.inatel.carmanager.exception;

public class BrandManagerConnectionException extends RuntimeException
{
    public BrandManagerConnectionException(String brandManagerBaseUrl)
    {
        super(
            String.format("Was not possible to communicate with brand-manager at location [%s]",
                          brandManagerBaseUrl));
    }
}
