package kr.ad.hansung.cse.ecommercespringrestjpa.exception;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8795546082074185666L;

    private Long Id;

    public NotFoundException(Long Id) {
        this.Id = Id;
    }

    public Long getId() {
        return Id;
    }
}